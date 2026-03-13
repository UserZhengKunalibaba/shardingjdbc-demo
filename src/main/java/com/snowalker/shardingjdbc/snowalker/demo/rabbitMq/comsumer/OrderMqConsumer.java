package com.snowalker.shardingjdbc.snowalker.demo.rabbitMq.comsumer;

import com.alibaba.fastjson.JSONObject;
import com.snowalker.shardingjdbc.snowalker.demo.config.RabbitConfig;
import com.snowalker.shardingjdbc.snowalker.demo.entity.OrderInfo;
import com.snowalker.shardingjdbc.snowalker.demo.mapper.OrderMapper;
import com.rabbitmq.client.Channel;
import groovy.util.logging.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OrderMqConsumer {

    @Autowired
    private OrderMapper orderMapper;

    @RabbitListener(queues = RabbitConfig.QUEUE_ORDER_CREATED)
    public void onMessage(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);

            OrderInfo orderInfo = JSONObject.parseObject(body, OrderInfo.class);

            orderMapper.addOrder(orderInfo);
            // TODO 你的业务逻辑

            channel.basicAck(tag, false);
        } catch (Exception e) {
            // 这里简单起见：失败就丢到死信/重试，你后续可以加重试队列
            channel.basicNack(tag, false, false);
            throw e;
        }
    }
}