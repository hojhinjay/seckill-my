package com.example.seckillmy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock{

    private final StringRedisTemplate stringRedisTemplate;
    private static final String KEY_PREFIX="lock:";
    private String username;

    @Autowired
    private RedisTemplate redisTemplate;

   public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate){
       this.username = name;
       this.stringRedisTemplate = stringRedisTemplate;
    }
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("lock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }


    private static final String ID_PREFIX = UUID.randomUUID().toString() + "-";
    @Override
    public boolean tryLock(Long timeoutSec) {
        // 获取线程标示
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        // 获取锁
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX + username, threadId + "", timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unLock() {
        // 调用lua脚本

        stringRedisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(KEY_PREFIX + username),
                ID_PREFIX + Thread.currentThread().getId());
    }
}
