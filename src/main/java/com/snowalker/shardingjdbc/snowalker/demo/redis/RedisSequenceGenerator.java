package com.snowalker.shardingjdbc.snowalker.demo.redis;

import com.snowalker.shardingjdbc.snowalker.demo.sequence.SequenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/3/25 9:09
 * @className RedisSequenceGenerator
 * @desc 序列生成器Redis实现
 */
@Component(value = "redisSequenceGenerator")
public class RedisSequenceGenerator implements SequenceGenerator {

    /**序列生成器key前缀*/
    public static String LOGIC_TABLE_NAME = "sequence:redis:";

    public static int SEQUENCE_LENGTH = 5;

    public static int sequence_max = 90000;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


}
