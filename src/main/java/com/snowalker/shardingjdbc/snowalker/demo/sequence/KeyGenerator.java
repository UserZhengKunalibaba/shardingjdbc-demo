package com.snowalker.shardingjdbc.snowalker.demo.sequence;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/3/25 9:17
 * @className KeyGenerator
 * @desc 自定义分布式主键生成器
 */
@Component
public class KeyGenerator {

    @Resource(name = "redisSequenceGenerator")
    SequenceGenerator sequenceGenerator;



}
