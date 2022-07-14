package com.atjgl.controller;

import com.atjgl.base.BaseInfoProperties;
import com.atjgl.bo.VlogBO;
import com.atjgl.enums.YesOrNo;
import com.atjgl.grace.result.GraceJSONResult;
import com.atjgl.grace.result.PagedGridResult;
import com.atjgl.mapper.VlogMapper;
import com.atjgl.pojo.Users;
import com.atjgl.pojo.Vlog;
import com.atjgl.service.impl.UserServiceImpl;
import com.atjgl.service.impl.VlogServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 小亮
 **/
@Slf4j
@RestController
@Api(tags = "VlogController vlog短视频功能模块")
@RequestMapping("vlog")
@RefreshScope
public class VlogController extends BaseInfoProperties {

    @Autowired
    private VlogServiceImpl vlogService;

    @Autowired
    private VlogMapper vlogMapper;

    @Autowired
    private UserServiceImpl userService;

    @Value("${nacos.counts}")
    private Integer nacosCounts;

    @ApiOperation(value = "视频上传进入数据库")
    @PostMapping("/publish")
    public GraceJSONResult publish(@Valid @RequestBody VlogBO vlogBO) {
        vlogService.createVlog(vlogBO);
        return GraceJSONResult.ok();
    }

    @ApiOperation(value = "获取视频列表")
    @GetMapping("/indexList")
    public GraceJSONResult indexList(@RequestParam String userId,
                                     @RequestParam(defaultValue = "") String search,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult indexVlogVOS = vlogService.queryVlogList(userId, search, page, pageSize);
        return GraceJSONResult.ok(indexVlogVOS);
    }

    @ApiOperation(value = "获取赞过的视频列表")
    @GetMapping("/myLikedList")
    public GraceJSONResult myLikedList(@RequestParam String userId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        return GraceJSONResult.ok(vlogService.getMyLikeList(userId, page, pageSize));
    }

    @ApiOperation(value = "展示视频详情")
    @GetMapping("/detail")
    public GraceJSONResult detail(@RequestParam(defaultValue = "") String userId,
                                     @RequestParam String vlogId) {
        return GraceJSONResult.ok(vlogService.queryVlogById(vlogId, userId));
    }
    @ApiOperation(value = "将视频转为公有")
    @PostMapping("/changeToPublic")
    public GraceJSONResult changeToPublic(@RequestParam String userId,
                                     @RequestParam String vlogId) {
        vlogService.changePrivateOrPublic(userId, vlogId, YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }
    @ApiOperation(value = "将视频转为私有")
    @PostMapping("/changeToPrivate")
    public GraceJSONResult changeToPrivate(@RequestParam String userId,
                                     @RequestParam String vlogId) {
        vlogService.changePrivateOrPublic(userId, vlogId, YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    @ApiOperation(value = "展示个人页面中私有视频")
    @GetMapping("/myPrivateList")
    public GraceJSONResult myPrivateList(@RequestParam String userId,
                                         @RequestParam Integer page,
                                         @RequestParam Integer pageSize) {
        PagedGridResult pagedGridResult = vlogService.queryMyVlogList(userId, page, pageSize, YesOrNo.YES.type);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @ApiOperation(value = "展示个人页面中作品")
    @GetMapping("/myPublicList")
    public GraceJSONResult myPublicList(@RequestParam String userId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize) {
        PagedGridResult pagedGridResult = vlogService.queryMyVlogList(userId, page, pageSize, YesOrNo.NO.type);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @ApiOperation(value = "为视频点赞")
    @PostMapping("/like")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId) {
        Users users = userService.queryUser(userId);
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(vlogerId);
        criteria.andEqualTo(vlogId);
        List<Vlog> vlogs = vlogMapper.selectByExample(example);
        if (users != null && vlogs != null && vlogs.size() != 0 && !vlogs.isEmpty()) {
            vlogService.like(userId, vlogId, vlogerId);
        }
        return GraceJSONResult.ok();
    }

    @ApiOperation(value = "取消点赞")
    @PostMapping("/unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId) {
        Users users = userService.queryUser(userId);
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(vlogerId);
        criteria.andEqualTo(vlogId);
        List<Vlog> vlogs = vlogMapper.selectByExample(example);
        if (users != null && vlogs != null && vlogs.size() != 0 && !vlogs.isEmpty()) {
            vlogService.unlike(userId, vlogId, vlogerId);
        }
        return GraceJSONResult.ok();
    }

    @ApiOperation(value = "刷新点赞数")
    @PostMapping("/totalLikedCounts")
    public GraceJSONResult totalLikedCounts(@RequestParam String vlogId) {
        String countStr = redisOperator.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        if (countStr != null && StringUtils.isNotBlank(countStr)) {
            Integer likeCounts = Integer.valueOf(countStr);
            if (likeCounts >= nacosCounts) {
                vlogService.flushLike(vlogId, likeCounts);
            }
        }
        return GraceJSONResult.ok(vlogService.getCountsOfLike(vlogId));
    }

    @ApiOperation(value = "获取关注页面的视频列表")
    @GetMapping("/followList")
    public GraceJSONResult followList(@RequestParam String myId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize) {
        PagedGridResult myFollowVlogList = vlogService.getMyFollowVlogList(myId, page, pageSize);
        return GraceJSONResult.ok(myFollowVlogList);
    }

    @ApiOperation(value = "获取朋友页面的视频列表")
    @GetMapping("/friendList")
    public GraceJSONResult friendList(@RequestParam String myId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult myFollowVlogList = vlogService.getMyFriendVlogList(myId, page, pageSize);
        return GraceJSONResult.ok(myFollowVlogList);
    }
}
