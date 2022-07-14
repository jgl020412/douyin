package com.atjgl.service;

import com.atjgl.grace.result.PagedGridResult;

/**
 * @author 小亮
 **/
public interface FansService {
    /**
     * 添加关注
     */
    public void doFollows(String myId, String vlogerId);

    /**
     * 取消关注
     */
    public void doCancel(String myId, String vlogerId);

    /**
     * 判断是否关注
     */
    public boolean isFollow(String myId, String vlogerId);

    /**
     * 查询关注列表
     */
    public PagedGridResult queryFollowList(String myId, Integer page, Integer pageSize);

    /**
     * 查询粉丝列表
     */
    public PagedGridResult queryFansList(String myId, Integer page, Integer pageSize);

}
