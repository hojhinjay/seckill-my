package com.example.seckillmy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckillmy.vo.GoodsVo;
import com.example.seckillmy.entity.TGoods;
import com.example.seckillmy.vo.updateGoods;

import java.util.List;

public interface ITGoodsService extends IService<TGoods> {

    /**
     * 返回商品列表
     *
     * @param
     * @return java.util.List<com.example.seckilldemo.vo.GoodsVo>
     * @author LC
     * @operation add
     * @date 5:49 下午 2022/3/3
     **/
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     *
     * @param goodsId
     * @return java.lang.String
     * @author LC
     * @operation add
     * @date 9:37 上午 2022/3/4
     **/
    GoodsVo findGoodsVobyGoodsId(Long goodsId);



    Integer updateGoodsMessage(updateGoods updateGoods);

}
