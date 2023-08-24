package com.example.seckillmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckillmy.entity.TGoods;
import com.example.seckillmy.vo.GoodsVo;
import com.example.seckillmy.vo.updateGoods;

import java.util.List;

/**
 * 商品表 Mapper 接口
 *
 * @author LiChao
 * @since 2022-03-03
 */
public interface TGoodsMapper extends BaseMapper<TGoods> {

    /**
     * 返回商品列表
     * @author LC
     * @operation add
     * @date 5:50 下午 2022/3/3
     * @param
     * @return java.util.List<com.example.seckilldemo.vo.GoodsVo>
     **/
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVobyGoodsId(Long goodsId);

    Integer updateGoodsMessage(updateGoods updateGoods);
}
