package com.example.seckillmy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 线程配置属性类
 *
 * @Author
 * @Date 2022-04-05 17:20
 **/
@Data
@Component
@ConfigurationProperties(prefix = "task.pool")
public class myThreadPoolConfig {
    private int corePoolSize;
    private int maxPoolSize;
    private int keepAliveSeconds;
    private int queueCapacity;
    private int awaitTerminationSeconds;
    private String threadNamePrefix;
}
