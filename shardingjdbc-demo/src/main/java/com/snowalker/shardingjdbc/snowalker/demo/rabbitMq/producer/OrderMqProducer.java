package com.snowalker.shardingjdbc.snowalker.demo.rabbitMq.producer;

import com.snowalker.shardingjdbc.snowalker.demo.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderMqProducer {

    private final RabbitTemplate rabbitTemplate;

    public OrderMqProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderCreated(String msg) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_ORDER,
                RabbitConfig.RK_ORDER_CREATED,
                msg
        );
    }
}
