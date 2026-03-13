package com.snowalker.shardingjdbc.snowalker.demo.controller;

import com.snowalker.shardingjdbc.snowalker.demo.entity.OrderInfo;
import com.snowalker.shardingjdbc.snowalker.demo.service.OrderService;
import com.snowalker.shardingjdbc.snowalker.demo.utils.IdGen;
import com.snowalker.shardingjdbc.snowalker.demo.utils.SnowflakeIdWorkerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    @ResponseBody
    public String createOrder(){

        orderService.addOrder();
        return "order created";
    }



}
