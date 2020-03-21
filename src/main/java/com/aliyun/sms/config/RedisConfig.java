package com.aliyun.sms.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.UnknownHostException;

/*
*   只需要自己创建出自己满意的序列化器放入容器中即可
*
* */
@Configuration
public class RedisConfig {

    @Bean("redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //修改默认（jdk）的序列化方式为json 方便可视化工具查看
        template.setDefaultSerializer(new GenericFastJsonRedisSerializer());
        return template;
    }
}
