package com.example.seckillmy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.example.seckillmy.config.AccessLimit;
import com.example.seckillmy.config.AccessLimit;
import com.example.seckillmy.entity.SeckillMessage;
import com.example.seckillmy.entity.TOrder;
import com.example.seckillmy.entity.TSeckillOrder;
import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.exception.GlobalException;
//import com.example.seckillmy.rabbitmq.MQSender;
import com.example.seckillmy.rabbitmq.MQSender;
import com.example.seckillmy.service.ITGoodsService;
import com.example.seckillmy.service.ITOrderService;
import com.example.seckillmy.service.ITSeckillOrderService;
import com.example.seckillmy.utils.JsonUtil;
import com.example.seckillmy.vo.GoodsVo;
import com.example.seckillmy.vo.RespBean;
import com.example.seckillmy.vo.RespBeanEnum;
//import com.example.seckillmy.vo.SeckillMessage;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀
 *
 * @author: LC
 * @date 2022/3/4 11:34 上午
 * @ClassName: SeKillController
 */
@Slf4j
@Controller
@RequestMapping("/seckill")
@Api(value = "秒杀", tags = "秒杀")
public class SeKillController implements InitializingBean {

    @Autowired
    private ITGoodsService itGoodsService;
    @Autowired
    private ITSeckillOrderService itSeckillOrderService;
    @Autowired
    private ITOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    @Qualifier(value="scriptStock")
    private RedisScript<Long> redisScript;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

//easyCaptcha
    @ApiOperation("获取验证码")
    @GetMapping(value = "/captcha")
    public void verifyCode(TUser tUser, Long goodsId, HttpServletResponse response) {
        if (tUser == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + tUser.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }

    /**
     * 获取秒杀地址
     *
     * @param tuser
     * @param goodsId
     * @param captcha
     * @return com.example.seckilldemo.vo.RespBean
     * @author LiChao
     * @operation add
     * @date 4:04 下午 2022/3/9
     **/
    @ApiOperation("获取秒杀地址")
//    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @GetMapping(value = "/path")
    @ResponseBody
    public RespBean getPath(TUser tuser, Long goodsId, String captcha, HttpServletRequest request) {
        if (tuser == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
//        ValueOperations valueOperations = redisTemplate.opsForValue();
        //限制访问次数，5秒内访问5次
//        String uri = request.getRequestURI();
//        captcha = "0";
//        Integer count = (Integer) valueOperations.get(uri + ":" + tuser.getId());
//        if (count == null) {
//            valueOperations.set(uri + ":" + tuser.getId(), 1, 5, TimeUnit.SECONDS);
//        } else if (count < 5) {
//            valueOperations.increment(uri + ":" + tuser.getId());
//        } else {
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
//        }


        boolean check = orderService.checkCaptcha(tuser, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        //根据userid跟goodsid创建唯一的路径，并且放入redis中
        String str = orderService.createPath(tuser, goodsId);
        return RespBean.success(str);
    }


    /**
     * 获取秒杀结果
     *
     * @param tUser
     * @param goodsId
     * @return orderId 成功 ；-1 秒杀失败 ；0 排队中
     * @author LiChao
     * @operation add
     * @date 7:04 下午 2022/3/8
     **/
    @ApiOperation("获取秒杀结果")
    @GetMapping("/getResult")
    @ResponseBody
    public RespBean getResult(TUser tUser, Long goodsId) {
        if (tUser == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = itSeckillOrderService.getResult(tUser, goodsId);
        return RespBean.success(orderId);
    }


    /**
     * 秒杀功能
     *
     * @param user
     * @param goodsId
     * @return java.lang.String
     * @author LC
     * @operation add
     * @date 11:36 上午 2022/3/4
     **/
    @Transactional
    @ApiOperation("秒杀功能")
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, TUser user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //优化后代码
        ValueOperations valueOperations = redisTemplate.opsForValue();
//        boolean check = orderService.checkPath(user, goodsId, path);
//        if (!check) {
//            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
//        }


//        //也是原子的，也可以用；保证只有十个进入下一个方法，如果并发小了，可能一个用户占了十分之二,这样后面redisson锁中会再次判断是否重复，但是redis中库存还是删除了，所以后面补上；
//        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
//        //预减库存,使用lua脚本保证获取、-- 库存是原子的，可以先判断一波，真正实现还得是乐观锁；
//        //Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
//        if (stock < 0) {
//            //EmptyStockMap.put(goodsId, true);
//            //使用decrement时候加上incr，那么每个小于0的都会+1，最终返回0
//            valueOperations.increment("seckillGoods:" + goodsId);
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }


        //后面都改用lua脚本一次性判断了；这里可以省略了；lua脚本中判断库存与重复，并且--；上面这里多了一项功能就是保证就十个请求；



        SeckillMessage seckillMessag = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessag));
        return RespBean.success(0);
    }


    /**
     * 秒杀功能-废弃2   redis中存了两种数据，一种是秒杀订单，下单成功就添加订单与秒杀单，同时秒杀单放入
     * redis种方便重复下单查询；  第二种是联查出商品情况以及库存(orderid,stock_num)方便流量消峰，后台慢慢处理
     *
     * @param user
     * @param goodsId
     * @return java.lang.String
     * @author LC
     * @operation add
     * @date 11:36 上午 2022/3/4
     **/
    @ApiOperation("秒杀功能")
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(TUser user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //判断是否重复抢购
        TSeckillOrder tSeckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (tSeckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //内存标记，减少Redis的访问
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存
        Long stock = (Long)  valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            EmptyStockMap.put(goodsId, true);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessag = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessag));
        return RespBean.success(0);
    }


        /**
         * 秒杀功能-废弃1
         *
         * @param model
         * @param user
         * @param goodsId
         * @return java.lang.String
         * @author LC
         * @operation add
         * @date 11:36 上午 2022/3/4
         **/
        @ApiOperation("秒杀功能")  //通过返回页面与数据，正常情况应该前端转发页面，再请求数据
        @RequestMapping(value = "/doSecKill3", method = RequestMethod.POST)
        public RespBean doSecKill3(Model model, TUser user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        GoodsVo goodsVo = itGoodsService.findGoodsVobyGoodsId(goodsId);
        if (goodsVo.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购,秒杀订单放入redis中减少对数据库的访问
//        TSeckillOrder seckillOrder = itSeckillOrderService.getOne(new QueryWrapper<TSeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
       //如果一个用户同时发送两个请求，那么这里都是0，会执行两次有问题

        TSeckillOrder seckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //新增订单表与秒杀订单表
            TOrder tOrder = null;
            try {
                tOrder = orderService.secKill(user, goodsVo);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return RespBean.success(tOrder);
}




    /**
     * 秒杀功能-废弃
     *
     * @param model
     * @param user
     * @param goodsId
     * @return java.lang.String
     * @author LC
     * @operation add
     * @date 11:36 上午 2022/3/4
     **/
    @ApiOperation("秒杀功能-废弃")  //通过返回页面与数据，正常情况应该前端转发页面，再请求数据
    @RequestMapping(value = "/doSeckill2", method = RequestMethod.POST)
    public String doSecKill2(Model model, TUser user, Long goodsId) {
        model.addAttribute("user", user);
        GoodsVo goodsVo = itGoodsService.findGoodsVobyGoodsId(goodsId);
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        //判断是否重复抢购
        TSeckillOrder seckillOrder = itSeckillOrderService.getOne(new QueryWrapper<TSeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        TOrder tOrder = null;
        try {
            tOrder = orderService.secKill(user, goodsVo);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("order", tOrder);
        model.addAttribute("goods", goodsVo);
        return "orderDetail";
    }

    /**
     * 系统初始化，把商品库存数量加载到Redis
     *
     * @param
     * @return void
     * @author LiChao
     * @operation add
     * @date 6:29 下午 2022/3/8
     **/
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = itGoodsService.findGoodsVo();//也是联表查询商品秒杀表跟商品表
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
