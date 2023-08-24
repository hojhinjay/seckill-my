package com.example.seckillmy.service;


import java.util.List;
import java.util.concurrent.TimeUnit;

public interface RedissonBloomFilterService {
    /**
     * 初始化一个布隆过滤器
     *
     * @param key        key
     * @param expireDate 过期时间长度
     * @param timeUnit   过期时间单位
     * @return 是否创建成功
     */
    boolean initNewFilter(String key, Long expireDate, TimeUnit timeUnit, long expectedInsertions, double falseProbability);

    /**
     * 添加value到今天布隆过滤器
     * @param key  key
     * @param value  需要添加到过滤器的值
     * @return  是否添加成功
     */
    boolean add(final String key, final String value);

    /**
     * 添加value到今天布隆过滤器
     * @param key  key
     * @param list  需要添加到过滤器的值
     * @return  是否添加成功
     */
    boolean add(final String key, final List<String> list);

    /**
     * 判断对应key是否有value值
     * @param key  key
     * @param value 对应key的value值
     * @return 应key是否有value值
     */
    boolean containsValue(final String key, final String value);

    /**
     * 查看是否存在key值
     * @param key  key
     * @return 是否有key值
     */
    boolean containKey(final String key);

}
