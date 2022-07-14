package com.atjgl.service;

import com.atjgl.bo.CommentBO;
import com.atjgl.grace.result.PagedGridResult;
import com.atjgl.pojo.Comment;
import com.atjgl.vo.CommentVO;

/**
 * @author 小亮
 **/
public interface CommentService {

    /**
     * 创建一个新的评论
     */
    public CommentVO createComment(CommentBO commentBO);

    /**
     * 获取评论列表
     */
    public PagedGridResult getCommentList(String vlogId, String userId, Integer page, Integer pageSize);

    /**
     * 删除评论
     */
    public void deleteComment(String userId, String vlogId, String commentId);

    /**
     * 获取一个评论
     */
    public Comment getComment(String id);

}
