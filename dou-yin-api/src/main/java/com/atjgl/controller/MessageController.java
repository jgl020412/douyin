package com.atjgl.controller;

import com.atjgl.base.BaseInfoProperties;
import com.atjgl.grace.result.GraceJSONResult;
import com.atjgl.service.impl.MsgServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 小亮
 **/
@Api(tags = "MessageController 消息相关的业务模块")
@RestController
@Slf4j
@RequestMapping("msg")
public class MessageController extends BaseInfoProperties {

    @Autowired
    private MsgServiceImpl msgService;

    @ApiOperation(value = "获取消息列表")
    @GetMapping("list")
    public GraceJSONResult list(@RequestParam String userId,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize) {
        return GraceJSONResult.ok(msgService.getMsgList(userId, page, pageSize));
    }

}
