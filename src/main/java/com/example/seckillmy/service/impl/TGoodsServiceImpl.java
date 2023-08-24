package com.example.seckillmy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckillmy.entity.TGoods;
import com.example.seckillmy.mapper.TGoodsMapper;
import com.example.seckillmy.service.ITGoodsService;
import com.example.seckillmy.vo.GoodsVo;
import com.example.seckillmy.mapper.TGoodsMapper;
import com.example.seckillmy.vo.GoodsVo;
import com.example.seckillmy.vo.updateGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Primary
public class TGoodsServiceImpl extends ServiceImpl<TGoodsMapper, TGoods> implements ITGoodsService {

    @Autowired
    private TGoodsMapper tGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<GoodsVo> findGoodsVo() {
        return tGoodsMapper.findGoodsVo();
    }

    @Override
    public GoodsVo findGoodsVobyGoodsId(Long goodsId) {
        String key = "cache:shop:"+goodsId;
        GoodsVo goodsVo = tGoodsMapper.findGoodsVobyGoodsId(goodsId);
       if(goodsVo == null ){
           //redisTemplate.opsForValue().set(key,""); 缓存穿透方案1：返回NULL同时查询时候也判断一下是否NULL；
           // 方案2：改用了布隆过滤器，直接在查询时候就判断了，这里不需要添加了；
           throw new RuntimeException("没有这个详细信息");
    }
        redisTemplate.opsForValue().set(key,goodsVo,30L, TimeUnit.MINUTES);
       return goodsVo;
    }

    @Override
    @Transactional
    public Integer updateGoodsMessage(updateGoods updateGoods) {

        String key = "cache:shop:"+updateGoods.getGoodsId();
        Integer i = this.getBaseMapper().updateGoodsMessage(updateGoods);
        redisTemplate.delete(key);
        return i;


    }
}
