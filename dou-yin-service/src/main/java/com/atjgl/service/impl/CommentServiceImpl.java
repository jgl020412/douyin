package com.atjgl.service.impl;

import com.atjgl.base.BaseInfoProperties;
import com.atjgl.base.RabbitMQConfig;
import com.atjgl.bo.CommentBO;
import com.atjgl.enums.MessageEnum;
import com.atjgl.enums.YesOrNo;
import com.atjgl.grace.result.PagedGridResult;
import com.atjgl.mapper.CommentMapper;
import com.atjgl.mapper.CommentMapperCustom;
import com.atjgl.mo.MessageMO;
import com.atjgl.pojo.Comment;
import com.atjgl.pojo.Vlog;
import com.atjgl.service.CommentService;
import com.atjgl.util.JsonUtils;
import com.atjgl.vo.CommentVO;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小亮
 **/

@Service
public class CommentServiceImpl extends BaseInfoProperties implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentMapperCustom commentMapperCustom;

    @Autowired
    private Sid sid;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private VlogServiceImpl vlogService;

    @Transactional
    @Override
    public CommentVO createComment(CommentBO commentBO) {
        Comment comment = new Comment();
        String id = sid.nextShort();
        comment.setId(id);
        comment.setCommentUserId(commentBO.getCommentUserId());
        comment.setVlogerId(commentBO.getVlogerId());
        comment.setFatherCommentId(commentBO.getFatherCommentId());
        comment.setCreateTime(new Date());
        comment.setVlogId(commentBO.getVlogId());
        comment.setContent(commentBO.getContent());
        comment.setLikeCounts(0);
        redisOperator.increment(REDIS_VLOG_COMMENT_COUNTS + ":" + commentBO.getVlogId(), 1);
        commentMapper.insert(comment);
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);
        MessageMO messageMO = new MessageMO();
        messageMO.setToUserId(comment.getVlogerId());
        Integer type = MessageEnum.COMMENT_VLOG.type;
        if (StringUtils.isNotBlank(commentBO.getFatherCommentId()) &&
                !commentBO.getFatherCommentId().equalsIgnoreCase("0")) {
            type = MessageEnum.REPLY_YOU.type;
            messageMO.setToUserId(getComment(commentBO.getFatherCommentId()).getCommentUserId());
        }
        Vlog vlog = vlogService.getVlog(commentBO.getVlogId());
        Map msgContent = new HashMap();
        msgContent.put("vlogId", vlog.getId());
        msgContent.put("vlogCover", vlog.getCover());
        msgContent.put("commentId", comment.getId());
        msgContent.put("commentContent", comment.getContent());

        messageMO.setFromUserId(comment.getCommentUserId());
        messageMO.setMsgType(type);
        messageMO.setMsgContent(msgContent);
        String payload = JsonUtils.objectToJson(messageMO);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,
                "sys.msg." + MessageEnum.FOLLOW_YOU.enValue,
                payload);

        return commentVO;
    }

    @Override
    public PagedGridResult getCommentList(String vlogId, String userId, Integer page, Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PageHelper.startPage(page, pageSize);
        HashMap<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);
        List<CommentVO> commentList = commentMapperCustom.getCommentList(map);
        for (CommentVO c : commentList) {
            String likeCountsStr = redisOperator.getHashValue(REDIS_VLOG_COMMENT_LIKED_COUNTS, c.getCommentId());
            Integer likeCounts = 0;
            if (StringUtils.isNotBlank(likeCountsStr)) {
                likeCounts = Integer.valueOf(likeCountsStr);
            }
            c.setLikeCounts(likeCounts);
            String isLike = redisOperator.getHashValue(REDIS_USER_LIKE_COMMENT, userId + ":" + c.getCommentId());
            if (StringUtils.isNotBlank(isLike) && isLike.equalsIgnoreCase("1")) {
                c.setIsLike(YesOrNo.YES.type);
            }
        }
        return setterPagedGrid(commentList, page);
    }

    @Transactional
    @Override
    public void deleteComment(String userId, String vlogId, String commentId) {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setCommentUserId(userId);
        commentMapper.delete(comment);
        redisOperator.decrement(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId, 1);
    }

    @Override
    public Comment getComment(String id) {
        return commentMapper.selectByPrimaryKey(id);
    }

}
