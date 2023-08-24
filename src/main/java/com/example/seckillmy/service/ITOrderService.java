package com.example.seckillmy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckillmy.entity.TOrder;
import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.vo.GoodsVo;
import com.example.seckillmy.vo.OrderDeatilVo;
import com.example.seckillmy.entity.TOrder;
import org.springframework.transaction.annotation.Transactional;

public interface ITOrderService extends IService<TOrder> {

    /**
     * 秒杀
     *
     * @param user    用户对象
     * @param goodsVo 商品对象
     * @return com.example.seckilldemo.entity.TOrder
     * @author LC
     * @operation add
     * @date 1:44 下午 2022/3/4
     **/
    TOrder secKill(TUser user, GoodsVo goodsVo) throws InterruptedException;

    @Transactional
    TOrder RealsecKill(TUser user, GoodsVo goodsVo) throws InterruptedException;

    /**
     * 订单详情方法
     *
     * @param orderId
     * @return com.example.seckilldemo.vo.OrderDeatilVo
     * @author LC
     * @operation add
     * @date 10:21 下午 2022/3/6
     **/
    OrderDeatilVo detail(Long orderId);

    /**
     * 获取秒杀地址
     *
     * @param user
     * @param goodsId
     * @return java.lang.String
     * @author LiChao
     * @operation add
     * @date 2:59 下午 2022/3/9
     **/
    String createPath(TUser user, Long goodsId);

    /**
     * 校验秒杀地址
     *
     * @param user
     * @param goodsId
     * @param path
     * @return boolean
     * @author LiChao
     * @operation add
     * @date 2:59 下午 2022/3/9
     **/
    boolean checkPath(TUser user, Long goodsId, String path);

    /**
     * 校验验证码
     * @author LiChao
     * @operation add
     * @date 3:52 下午 2022/3/9
     * @param tuser
     * @param goodsId
     * @param captcha
     * @return boolean
     **/
    boolean checkCaptcha(TUser tuser, Long goodsId, String captcha);
}
