package com.snowalker.shardingjdbc.snowalker.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.snowalker.shardingjdbc.snowalker.demo.entity.OrderInfo;
import com.snowalker.shardingjdbc.snowalker.demo.utils.IdGen;
import com.snowalker.shardingjdbc.snowalker.demo.mapper.OrderMapper;
import com.snowalker.shardingjdbc.snowalker.demo.rabbitMq.producer.OrderMqProducer;
import com.snowalker.shardingjdbc.snowalker.demo.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/3/3 22:39
 * @className
 * @desc
 */
@Service(value = "orderService")
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderMqProducer orderMqProducer;

    @Override
    public List<OrderInfo> queryOrderInfoList(OrderInfo orderInfo) {
        return orderMapper.queryOrderInfoList(orderInfo);
    }

    @Override
    public OrderInfo queryOrderInfoByOrderId(OrderInfo orderInfo) {
        return orderMapper.queryOrderInfoByOrderId(orderInfo);
    }

    @Override
    public int addOrder() {
        OrderInfo orderInfo = new OrderInfo();
        IdGen idGen = new IdGen();
        long l = idGen.nextLong();
        orderInfo.setOrderId(l+"");
        orderInfo.setUserId(l+"");
        orderInfo.setId(l+"");
        orderInfo.setUserName("123");
        LOGGER.info("订单入库开始，orderinfo={}", orderInfo.toString());

        orderMqProducer.sendOrderCreated(JSONObject.toJSONString(orderInfo));
        return orderMapper.addOrder(orderInfo);
    }

    @Override
    public int addOrderASync() {
        OrderInfo orderInfo = new OrderInfo();
        IdGen idGen = new IdGen();
        long l = idGen.nextLong();
        orderInfo.setOrderId(l+"");
        orderInfo.setUserId(l+"");
        orderInfo.setId(l+"");
        orderInfo.setUserName("123");
        orderMqProducer.sendOrderCreated(JSONObject.toJSONString(orderInfo));
        return 0;
    }
}
