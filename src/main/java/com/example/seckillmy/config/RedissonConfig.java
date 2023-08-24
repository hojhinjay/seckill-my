package com.example.seckillmy.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
//https://www.freesion.com/article/58371034668/
    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.198.134:6379").setPassword("123456");

        // 使用json序列化方式
        Codec codec = new JsonJacksonCodec();
        config.setCodec(codec);
        // 创建RedissonClient对象
        return Redisson.create(config);
    }





}
