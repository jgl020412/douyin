package com.atjgl.service;

import com.atjgl.mo.MessageMO;

import java.util.List;

/**
 * @author 小亮
 **/
public interface MsgService {
    /**
     * 创建一个消息
     */
    public void createMessage(MessageMO messageMO);

    /**
     * 获取消息列表
     */
    public List<MessageMO> getMsgList(String userId, Integer page, Integer pageSize);

}
