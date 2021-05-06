package com.lijuncai.learningbbs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @description: Redis配置类，虽然Spring内置了Redis配置类，但其Key的类型为Object
 * 在Redis中Key都是String类型，为了更方便使用，因此自己构建一个配置类
 * @author: lijuncai
 **/

@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate
     *
     * @param factory Redis连接工厂
     * @return RedisTemplate对象
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());

        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //触发生效
        template.afterPropertiesSet();
        return template;
    }
}
