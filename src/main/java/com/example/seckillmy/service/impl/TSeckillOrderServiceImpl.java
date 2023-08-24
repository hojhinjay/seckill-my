package com.example.seckillmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckillmy.entity.TSeckillOrder;
import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.mapper.TSeckillOrderMapper;
import com.example.seckillmy.service.ITSeckillOrderService;
import com.example.seckillmy.entity.TSeckillOrder;
import com.example.seckillmy.mapper.TSeckillOrderMapper;
import com.example.seckillmy.service.ITSeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Primary
public class TSeckillOrderServiceImpl extends ServiceImpl<TSeckillOrderMapper, TSeckillOrder> implements ITSeckillOrderService {

    @Autowired
    private TSeckillOrderMapper tSeckillOrderMapper;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Long getResult(TUser tUser, Long goodsId) {
//查询秒杀订单 -1失败  0排队中
        TSeckillOrder tSeckillOrder = tSeckillOrderMapper.selectOne(new QueryWrapper<TSeckillOrder>().eq("user_id", tUser.getId()).eq("goods_id", goodsId));
        if (null != tSeckillOrder) {
            return tSeckillOrder.getOrderId();
        } else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)) {
            return -1L;
        } else {
            return 0L;
        }

    }
}
