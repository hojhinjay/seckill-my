package com.example.seckillmy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckillmy.entity.TSeckillGoods;
import com.example.seckillmy.mapper.TSeckillGoodsMapper;
import com.example.seckillmy.service.ITSeckillGoodsService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class TSeckillGoodsServiceImpl extends ServiceImpl<TSeckillGoodsMapper, TSeckillGoods> implements ITSeckillGoodsService {

}
