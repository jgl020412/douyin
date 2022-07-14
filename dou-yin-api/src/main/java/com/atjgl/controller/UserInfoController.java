package com.atjgl.controller;

import com.atjgl.MinIOConfig;
import com.atjgl.base.BaseInfoProperties;
import com.atjgl.bo.UpdatedUsersBO;
import com.atjgl.enums.FileTypeEnum;
import com.atjgl.enums.UserInfoModifyType;
import com.atjgl.grace.result.GraceJSONResult;
import com.atjgl.grace.result.ResponseStatusEnum;
import com.atjgl.pojo.Users;
import com.atjgl.service.impl.UserServiceImpl;
import com.atjgl.util.MinIOUtils;
import com.atjgl.vo.UsersVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 小亮
 **/
@RestController
@Api(tags = "UserInfoController 用户信息接口模块")
@Slf4j
@RequestMapping("userInfo")
public class UserInfoController extends BaseInfoProperties {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MinIOConfig minIOConfig;

    @ApiOperation(value = "查询用户信息")
    @GetMapping("/query")
    public GraceJSONResult query(@RequestParam String userId) {
        Users users = userService.queryUser(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users, usersVO);
        String myFollowsCountsStr = redisOperator.get(REDIS_MY_FOLLOWS_COUNTS + ":" + userId);
        String myFansCountsStr = redisOperator.get(REDIS_MY_FANS_COUNTS + ":" + userId);
        String likedVlogerCountsStr = redisOperator.get(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + userId);

        Integer myFollowsCounts = 0;
        Integer myFansCounts = 0;
        Integer totalLikeMeCounts = 0;

        if (StringUtils.isNotBlank(myFollowsCountsStr)) {
            myFollowsCounts = Integer.valueOf(myFollowsCountsStr);
        }
        if (StringUtils.isNotBlank(myFansCountsStr)) {
            myFansCounts = Integer.valueOf(myFansCountsStr);
        }
        if (StringUtils.isNotBlank(likedVlogerCountsStr)) {
            totalLikeMeCounts = Integer.valueOf(likedVlogerCountsStr);
        }

        usersVO.setMyFollowsCounts(myFollowsCounts);
        usersVO.setMyFansCounts(myFansCounts);
        usersVO.setTotalLikeMeCounts(totalLikeMeCounts);

        return GraceJSONResult.ok(usersVO);
    }

    @ApiOperation(value = "修改用户信息")
    @PostMapping("/modifyUserInfo")
    public GraceJSONResult modifyUserInfo(@RequestBody UpdatedUsersBO updatedUsersBO, @RequestParam Integer type) {
        UserInfoModifyType.checkUserInfoTypeIsRight(type);
        Users users = userService.updateUserInfo(updatedUsersBO, type);
        return GraceJSONResult.ok(users);
    }

    @ApiOperation(value = "修改图片")
    @PostMapping("/modifyImage")
    public GraceJSONResult modifyImage(@RequestParam String userId,
                                       @RequestParam Integer type, MultipartFile file) throws Exception {
        if (type != FileTypeEnum.FACE.type && type != FileTypeEnum.BGIMG.type) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());
        String imgUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/"
                + filename;

        UpdatedUsersBO updatedUsersBO = new UpdatedUsersBO();
        updatedUsersBO.setId(userId);
        if (type == FileTypeEnum.FACE.type) {
            updatedUsersBO.setFace(imgUrl);
        } else {
            updatedUsersBO.setBgImg(imgUrl);
        }

        Users users = userService.updateUserInfo(updatedUsersBO);

        return GraceJSONResult.ok(users);
    }

}