package com.atjgl.base;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 小亮
 **/

@Configuration
public class RabbitMQConfig {

    // 定义交换机的名字
    public static final String EXCHANGE_MSG = "exchange_msg";

    // 定义队列名字
    public static final String QUEUE_MSG = "queue_msg";

    // 创建交换机，并放入springboot容器中
    @Bean(EXCHANGE_MSG)
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_MSG).durable(true).build();
    }

    // 创建消息队列，并放入springboot容器中
    @Bean(QUEUE_MSG)
    public Queue queue() {
        return new Queue(QUEUE_MSG);
    }

    // 队列绑定交换机
    @Bean
    public Binding binding(@Qualifier(EXCHANGE_MSG) Exchange exchange,
                           @Qualifier(QUEUE_MSG) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("sys.msg.*").noargs();
    }

}
