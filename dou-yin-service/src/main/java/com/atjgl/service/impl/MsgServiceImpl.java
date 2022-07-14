package com.atjgl.service.impl;

import com.atjgl.base.BaseInfoProperties;
import com.atjgl.enums.MessageEnum;
import com.atjgl.mo.MessageMO;
import com.atjgl.pojo.Users;
import com.atjgl.repository.MessageRepository;
import com.atjgl.service.MsgService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小亮
 **/

@Service
public class MsgServiceImpl extends BaseInfoProperties implements MsgService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void createMessage(MessageMO messageMO) {
        String fromUserId = messageMO.getFromUserId();
        Users users = userService.queryUser(fromUserId);
        messageMO.setFromNickname(users.getNickname());
        messageMO.setFromFace(users.getFace());

        messageMO.setCreateTime(new Date());

        messageRepository.save(messageMO);
    }

    @Override
    public List<MessageMO> getMsgList(String userId, Integer page, Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE_ZERO;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createTime");
        List<MessageMO> list = messageRepository.findAllByToUserIdEqualsOrderByCreateTimeDesc(userId, pageable);
        for (MessageMO msg : list) {
            if (msg.getMsgType() == MessageEnum.FOLLOW_YOU.type) {
                Map msgContent = msg.getMsgContent();
                if (msgContent == null) {
                    msgContent = new HashMap();
                }
                String relationShip = redisOperator.get(REDIS_MY_AND_VLOGER_RELATIONSHIP
                                + ":" + msg.getToUserId() + ":" + msg.getFromUserId());
                if (StringUtils.isNotBlank(relationShip) && relationShip.equalsIgnoreCase("1")) {
                    msgContent.put("isFriend", true);
                } else {
                    msgContent.put("isFriend", false);
                }
                msg.setMsgContent(msgContent);
            }
        }
        return list;
    }
}
