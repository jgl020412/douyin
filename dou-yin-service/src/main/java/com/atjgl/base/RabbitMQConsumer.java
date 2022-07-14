package com.atjgl.base;

import com.atjgl.enums.MessageEnum;
import com.atjgl.exception.GraceException;
import com.atjgl.grace.result.ResponseStatusEnum;
import com.atjgl.mo.MessageMO;
import com.atjgl.service.impl.MsgServiceImpl;
import com.atjgl.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 小亮
 **/

@Service
@Slf4j
public class RabbitMQConsumer {

    @Autowired
    private MsgServiceImpl msgService;

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_MSG})
    public void watchQueue(String payload, Message message) {
        log.info(payload);
        MessageMO messageMO = JsonUtils.jsonToPojo(payload, MessageMO.class);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info(routingKey);
        if (routingKey.equals("sys.msg." + MessageEnum.FOLLOW_YOU.enValue)) {
            msgService.createMessage(messageMO);
        } else if (routingKey.equals("sys.msg." + MessageEnum.LIKE_VLOG.enValue)) {
            msgService.createMessage(messageMO);
        } else if (routingKey.equals("sys.msg." + MessageEnum.COMMENT_VLOG.enValue)) {
            msgService.createMessage(messageMO);
        } else if (routingKey.equals("sys.msg." + MessageEnum.REPLY_YOU.enValue)) {
            msgService.createMessage(messageMO);
        } else if (routingKey.equals("sys.msg." + MessageEnum.LIKE_COMMENT.enValue)) {
            msgService.createMessage(messageMO);
        } else {
            GraceException.display(ResponseStatusEnum.SYSTEM_ERROR);
        }
    }

}
