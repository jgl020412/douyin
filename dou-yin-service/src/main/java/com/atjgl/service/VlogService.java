package com.atjgl.service;

import com.atjgl.bo.VlogBO;
import com.atjgl.grace.result.PagedGridResult;
import com.atjgl.pojo.Vlog;
import com.atjgl.vo.IndexVlogVO;

/**
 * @author 小亮
 **/

public interface VlogService {

    /**
     * 创建一个新的视频
     */
    public void createVlog(VlogBO vlogBO);

    /**
     * 查询一个页面的视频列表
     */
    public PagedGridResult queryVlogList(String userId, String search, Integer page, Integer pageSize);

    /**
     * 查询一个页面的视频列表
     */
    public IndexVlogVO queryVlogById(String id, String userId);

    /**
     * 获取赞过视频列表
     */
    public PagedGridResult getMyLikeList(String userId, Integer page, Integer pageSize);

    /**
     * 修改一个视频为私有的或者是公开的
     */
    public void changePrivateOrPublic(String userId, String vlogId, Integer yesOrNo);

    /**
     * 修改一个视频为私有的或者是公开的
     */
    public PagedGridResult queryMyVlogList(String userId,Integer page, Integer pageSize, Integer yesOrNo);

    /**
     * 点赞视频
     */
    public void like(String myId, String vlogId, String vlogerId);

    /**
     * 取消点赞视频
     */
    public void unlike(String myId, String vlogId, String vlogerId);

    /**
     * 获取点赞数
     */
    public Integer getCountsOfLike(String vlogId);

    /**
     * 获取关注中的视频列表
     */
    public PagedGridResult getMyFollowVlogList(String myId, Integer page, Integer pageSize);

    /**
     * 获取朋友中的视频列表
     */
    public PagedGridResult getMyFriendVlogList(String myId, Integer page, Integer pageSize);

    /**
     * 获取指定视频
     */
    public Vlog getVlog(String id);

    /**
     * 将视频的点赞数输入进数据库
     */
    public void flushLike(String vlogeId, Integer counts);

}
