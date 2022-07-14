package com.atjgl.controller;

import com.atjgl.base.BaseInfoProperties;
import com.atjgl.grace.result.GraceJSONResult;
import com.atjgl.grace.result.ResponseStatusEnum;
import com.atjgl.pojo.Users;
import com.atjgl.service.impl.FansServiceImpl;
import com.atjgl.service.impl.UserServiceImpl;
import com.atjgl.util.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 小亮
 **/

@Slf4j
@Api(tags = "FansController 粉丝相关业务的功能模块")
@RequestMapping("fans")
@RestController
public class FansController extends BaseInfoProperties {

    @Autowired
    private FansServiceImpl fansService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "关注用户")
    @PostMapping("/follow")
    public GraceJSONResult follow(@RequestParam String myId, @RequestParam String vlogerId) {
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }
        if (myId.equals(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }
        Users my = userService.queryUser(myId);
        Users vloger = userService.queryUser(vlogerId);
        if (my == null || vloger == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        fansService.doFollows(myId, vlogerId);

        return GraceJSONResult.ok();
    }

    @ApiOperation(value = "取消关注")
    @PostMapping("/cancel")
    public GraceJSONResult cancel(@RequestParam String myId, @RequestParam String vlogerId) {
        fansService.doCancel(myId, vlogerId);
        return GraceJSONResult.ok();
    }

    @ApiOperation(value = "判断是否关注")
    @GetMapping("/queryDoIFollowVloger")
    public GraceJSONResult queryDoIFollowVloger(@RequestParam String myId, @RequestParam String vlogerId) {
        boolean follow = fansService.isFollow(myId, vlogerId);
        return  GraceJSONResult.ok(follow);
    }

    @ApiOperation(value = "查看粉丝列表")
    @GetMapping("/queryMyFans")
    public GraceJSONResult queryMyFans(@RequestParam String myId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize) {
        return GraceJSONResult.ok(fansService.queryFansList(myId, page, pageSize));
    }

    @ApiOperation(value = "查看关注列表")
    @GetMapping("/queryMyFollows")
    public GraceJSONResult queryMyFollows(@RequestParam String myId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize) {
        return GraceJSONResult.ok(fansService.queryFollowList(myId, page, pageSize));
    }

}
