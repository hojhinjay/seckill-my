package com.example.seckillmy.service.impl;

import com.example.seckillmy.service.ITGoodsService;
import com.example.seckillmy.service.RedissonBloomFilterService;
import com.example.seckillmy.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedissonBloomServiceImpl implements RedissonBloomFilterService {

    public static String prefix = "redission_bf_";


    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int TIME_FORMAT_LENGTH = 8;

    @Resource
    private RedissonClient redissonClient;


    @Autowired
    private ITGoodsService itGoodsService;

//Constructor >> @Autowired >> @PostConstruct
//    @PostConstruct
//    public void initBloom(){
//
//        if(!initNewFilter("bloomkey1", 60L, TimeUnit.MINUTES, 50, 0.01)){
//            throw new RuntimeException("filter failed!!!!!!!!!!!!!");
//        }
//
//        for(GoodsVo goodsVo:itGoodsService.findGoodsVo()){
//            add("bloomkey1", "" + goodsVo.getId());//把商品的id传进去
//        }
//
//    }

    /**
     * 1、第一层的key为传入key值
     * 2、第二次key为  prefix + key+20200718
     */
    private Map<String, ConcurrentHashMap<String, RBloomFilter>> bloomFilterMap = new ConcurrentHashMap<>();

    @Override
    public synchronized boolean initNewFilter(String key, Long expireDate, TimeUnit timeUnit, long expectedInsertions, double falseProbability) {

        String timeFormatter = formatter.format(LocalDateTime.now());
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(prefix + timeFormatter + key);
        //初始化布隆过滤器
        boolean isSuccess = bloomFilter.tryInit(expectedInsertions, falseProbability);

        if (isSuccess) {
            //设置过期时间
            bloomFilter.expire(expireDate, timeUnit);

            //设置过期监听器
            bloomFilter.addListener(new ExpiredObjectListener() {
                @Override
                public void onExpired(String key) {
                    log.info("onExpired callback running key is {}", key);
                    if (key.startsWith(prefix)) {
                        String firstKey = key.substring(prefix.length() + TIME_FORMAT_LENGTH);
                        ConcurrentHashMap<String, RBloomFilter> map = bloomFilterMap.get(firstKey);
                        if (Objects.isNull(map)) {
                            log.warn("listener callback key is empty key is {}", key);
                            return;
                        }
                        map.remove(key);
                    }
                }
            });
            ConcurrentHashMap<String, RBloomFilter> map = bloomFilterMap.computeIfAbsent(key, (k) -> {
                return new ConcurrentHashMap<String, RBloomFilter>(4);
            });
            map.put(prefix + timeFormatter + key, bloomFilter);
            isSuccess = true;
        }else {
            log.error("initNewFilter fail key is {}", key);
        }
        return isSuccess;
    }

    @Override
    public boolean add(String key, String value) {
        String timeFormatter = formatter.format(LocalDateTime.now());
        ConcurrentHashMap<String, RBloomFilter> map = bloomFilterMap.get(key);
        if (Objects.isNull(map)) {
            log.error("add name key one value is null, name is {},value is {}", key, value);
            return false;
        }
        RBloomFilter rBloomFilter = map.get(prefix + timeFormatter + key);
        if (Objects.isNull(rBloomFilter)) {
            log.error("add name key one value is null, name is {},redis key is {},value is {}", key, prefix + timeFormatter + key, value);
            return false;
        }
        return rBloomFilter.add(value);
    }

    @Override
    public boolean add(String key, List<String> list) {
        String timeFormatter = formatter.format(LocalDateTime.now());
        ConcurrentHashMap<String, RBloomFilter> map = bloomFilterMap.get(key);
        if (Objects.isNull(map)) {
            log.error("add name key list value is null, name is {}", key);
            return false;
        }
        RBloomFilter rBloomFilter = map.get(prefix + timeFormatter + key);
        if (Objects.isNull(rBloomFilter)) {
            log.error("add name key list value is null, name is {},redis key is {}", key,prefix + timeFormatter + key);
            return false;
        }
        list.forEach(value -> rBloomFilter.add(value));
        return true;
    }

    @Override
    public boolean containsValue(String key, String value) {
        ConcurrentHashMap<String, RBloomFilter> map = bloomFilterMap.get(key);
        if (Objects.isNull(map)) {
            log.error("containsValue key is exist, key is {}", key);
            return false;
        }
        for (Map.Entry<String, RBloomFilter> entry : map.entrySet()) {
            if (entry.getValue().contains(value)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean containKey(String key) {
        return bloomFilterMap.containsKey(key);
    }

}

