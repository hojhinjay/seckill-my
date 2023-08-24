package com.example.seckillmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckillmy.entity.TOrder;
import com.example.seckillmy.entity.TSeckillGoods;
import com.example.seckillmy.entity.TSeckillOrder;
import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.exception.GlobalException;
import com.example.seckillmy.mapper.TOrderMapper;
import com.example.seckillmy.service.ITGoodsService;
import com.example.seckillmy.service.ITOrderService;
import com.example.seckillmy.service.ITSeckillGoodsService;
import com.example.seckillmy.service.ITSeckillOrderService;
import com.example.seckillmy.utils.MD5Util;
import com.example.seckillmy.utils.UUIDUtil;
import com.example.seckillmy.vo.GoodsVo;
import com.example.seckillmy.vo.OrderDeatilVo;
import com.example.seckillmy.vo.RespBeanEnum;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.*;
@Service
@Primary
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements ITOrderService {

    @Autowired
    private ITSeckillGoodsService itSeckillGoodsService;
    @Autowired
    private ITSeckillOrderService itSeckillOrderService;
    @Autowired
    private ITGoodsService itGoodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    @Qualifier("customAsyncThreadPool")
    private ThreadPoolTaskExecutor customAsyncThreadPool;

    private static final DefaultRedisScript<Long> seckill;
    static {
        seckill = new DefaultRedisScript<>();
        seckill.setLocation(new ClassPathResource("seckill.lua"));
        seckill.setResultType(Long.class);
    }


    @Override
    public TOrder secKill(TUser user, GoodsVo goodsVo) throws InterruptedException {

        // 1.执行Lua脚本,可以替代MQ controller中的库存与重复，但是仍然无法解决非原子下单操作,还是只能让一个线程上锁解决；
        // 该脚本只判断库存跟重复，并且成功就 redis-1 库存，订单放入redis中还是在后面订单代码中执行
        Long result = stringRedisTemplate.execute(seckill, Collections.emptyList(), goodsVo.getId().toString(), user.getId().toString());
        // 2.判断结果是不为0
        if (result.intValue() != 0) {
            return null;
        }
            // 2.1.不为0，代表没有购买资格
            // 2.2.为0，有购买资格，把下单信息保存到明塞队列
            CompletableFuture<?> completableFuture =CompletableFuture.supplyAsync(()->{
                try {
                    return RealsecKill(user,goodsVo);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            },customAsyncThreadPool).thenApply((preResult)->{
                    return preResult;
            }).exceptionally((e)->{
                e.printStackTrace();
                System.out.printf("异步任务异常情况");
                return null;
            });
        try {
            return (TOrder) completableFuture.get(5,TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }


        @Transactional
        @Override
        public TOrder RealsecKill(TUser user, GoodsVo goodsVo) throws InterruptedException {
        // 1.userid  goodsid联合唯一索引 ： insert时候会发现同一个人多个订单问题(或者在分布式锁中再次查询判断);
        // 2.使用分布式锁来解决，对共资源使用redis的锁来锁住分布式，仅仅syn只能锁住单体；
        // 3.这里将秒杀订单放入redis中只是减少压力，使用lua脚本仅仅保证了redis -- 的原子性但是如下面情况所示：
        //想要用redis来阻止也会有并发情况；同一个用户的当执行到订单放入redis前一步，(另外的一个请求，这里暂时没多线程代码)另一个线程进来判断redis中是否有数据就会跳过去生成订单

       //确保同一用户抢相同资源锁
//        SimpleRedisLock lock = new SimpleRedisLock("order:"+user.getId(),stringRedisTemplate);
        RLock lock = redissonClient.getLock("order:"+user.getId());


       if(!lock.tryLock() ){//尝试获得锁，lua只是为了保证原子性，setIfAbsent才是上锁；
           //加上时间，在这个时间内再次自旋锁，要不然直接返回null就没办法再次判断是不是同一个人的订单，并且补上redis;否则只补上一次+1，其他同一用户的都直接tryLock null了；
           // 但是吧，这样做性能就下来了，每个阻塞的都会去重新判断+1；实际上也没这种全是一个用户的情况；
            //return RespBean.error(RespBeanEnum.REPEATE_ERROR);
             return null;
       }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        TSeckillGoods seckillGoods = itSeckillGoodsService.getOne(new QueryWrapper<TSeckillGoods>().eq("goods_id", goodsVo.getId()));
       try {
            //判断是否重复抢购
            TSeckillOrder tSeckillOrder1 = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
            if (tSeckillOrder1 != null) {
           // redis中补回来,补回来之后如果再并发则可以重新秒杀了；但是大多数情况是：同时两个请求，另一个获取不到锁直接失败，白 -- 了；
                valueOperations.increment("seckillGoods:" + goodsVo.getId());
                return null;
            }


           //秒杀商品表减去库存:
           //为了解决超卖，1.乐观锁  2.库存放入redis中减少MySQL访问
           boolean seckillGoodsResult = itSeckillGoodsService.update(new UpdateWrapper<TSeckillGoods>()
                   .setSql("stock_count = " + "stock_count-1")
                   .eq("goods_id", goodsVo.getId())  //where goods_id =  ?
                   .gt("stock_count", 0)   //  >0
           );  //结果马上跟新，事务没提交数据库没有跟新，下面的SQL会查到新的结果
           if (!seckillGoodsResult) {
               return null;
           }


            TOrder order = new TOrder();
            order.setUserId(user.getId());
            order.setGoodsId(goodsVo.getId());
            order.setDeliveryAddrId(0L);
            order.setGoodsName(goodsVo.getGoodsName());
            order.setGoodsCount(1);
            order.setGoodsPrice(seckillGoods.getSeckillPrice());
            order.setOrderChannel(1);
            order.setStatus(0);
            order.setCreateDate(new Date());
            this.getBaseMapper().insert(order);
            //生成秒杀订单
            TSeckillOrder tSeckillOrder = new TSeckillOrder();
            tSeckillOrder.setUserId(user.getId());
            tSeckillOrder.setOrderId(order.getId());
            tSeckillOrder.setGoodsId(goodsVo.getId());
            itSeckillOrderService.save(tSeckillOrder);
            redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goodsVo.getId(), tSeckillOrder);
            return order;
        }finally {
           lock.unlock();
        }
    }

    @Override
    public OrderDeatilVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        TOrder tOrder = this.getBaseMapper().selectById(orderId);
        GoodsVo goodsVobyGoodsId = itGoodsService.findGoodsVobyGoodsId(tOrder.getGoodsId());
        OrderDeatilVo orderDeatilVo = new OrderDeatilVo();
        orderDeatilVo.setTOrder(tOrder);
        orderDeatilVo.setGoodsVo(goodsVobyGoodsId);
        return orderDeatilVo;
    }

    @Override
    public String createPath(TUser user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 1, TimeUnit.MINUTES);
        return str;
    }

    @Override
    public boolean checkPath(TUser user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    @Override
    public boolean checkCaptcha(TUser user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(captcha)) {
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
