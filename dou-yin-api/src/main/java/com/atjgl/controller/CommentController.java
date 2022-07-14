package com.atjgl.controller;

import com.atjgl.base.BaseInfoProperties;
import com.atjgl.base.RabbitMQConfig;
import com.atjgl.bo.CommentBO;
import com.atjgl.enums.MessageEnum;
import com.atjgl.grace.result.GraceJSONResult;
import com.atjgl.grace.result.PagedGridResult;
import com.atjgl.mo.MessageMO;
import com.atjgl.pojo.Comment;
import com.atjgl.pojo.Vlog;
import com.atjgl.service.CommentService;
import com.atjgl.service.impl.VlogServiceImpl;
import com.atjgl.util.JsonUtils;
import com.atjgl.vo.CommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 小亮
 **/
@Slf4j
@Api(tags = "CommentController 评论相关的业务模块")
@RestController
@RequestMapping("comment")
public class CommentController extends BaseInfoProperties {

    @Autowired
    private CommentService commentService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private VlogServiceImpl vlogService;

    @ApiOperation(value = "创建一个评论")
    @PostMapping("/create")
    public GraceJSONResult create(@RequestBody @Valid CommentBO commentBO) {
        CommentVO comment = commentService.createComment(commentBO);
        return GraceJSONResult.ok(comment);
    }

    @ApiOperation(value = "显示评论数量")
    @GetMapping("/counts")
    public GraceJSONResult counts(@RequestParam String vlogId) {
        String count = redisOperator.get(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId);
        return GraceJSONResult.ok(count);
    }

    @ApiOperation(value = "获取评论列表")
    @GetMapping("list")
    public GraceJSONResult list(@RequestParam String vlogId,
                                @RequestParam String userId,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize) {
        PagedGridResult commentList = commentService.getCommentList(vlogId, userId, page, pageSize);
        return GraceJSONResult.ok(commentList);
    }

    @ApiOperation(value = "删除评论")
    @DeleteMapping("delete")
    public GraceJSONResult delete(@RequestParam String commentUserId,
                                  @RequestParam String commentId,
                                  @RequestParam String vlogId) {
        commentService.deleteComment(commentUserId, vlogId, commentId);
        return GraceJSONResult.ok();
    }

    @ApiOperation(value = "点赞评论")
    @PostMapping("like")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String commentId) {
        redisOperator.incrementHash(REDIS_VLOG_COMMENT_LIKED_COUNTS, commentId, 1);
        redisOperator.setHashValue(REDIS_USER_LIKE_COMMENT, userId + ":" + commentId, "1");

        Comment comment = commentService.getComment(commentId);
        Vlog vlog = vlogService.getVlog(comment.getVlogId());
        Map msgContent = new HashMap();
        msgContent.put("commentId", commentId);
        msgContent.put("vlogId", vlog.getId());
        msgContent.put("vlogCover", vlog.getCover());
        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(userId);
        messageMO.setToUserId(comment.getCommentUserId());
        messageMO.setMsgType(MessageEnum.LIKE_COMMENT.type);
        messageMO.setMsgContent(msgContent);
        String payload = JsonUtils.objectToJson(messageMO);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,
                "sys.msg." + MessageEnum.FOLLOW_YOU.enValue,
                payload);
        return GraceJSONResult.ok();
    }

    @ApiOperation(value = "删除评论")
    @PostMapping("unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                @RequestParam String commentId) {
        redisOperator.decrementHash(REDIS_VLOG_COMMENT_LIKED_COUNTS, commentId, 1);
        redisOperator.hdel(REDIS_USER_LIKE_COMMENT, userId + ":" + commentId, "1");
        return GraceJSONResult.ok();
    }
}
