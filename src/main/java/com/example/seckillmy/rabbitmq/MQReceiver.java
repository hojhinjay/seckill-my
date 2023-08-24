package com.example.seckillmy.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.seckillmy.entity.SeckillMessage;
import com.example.seckillmy.entity.TSeckillOrder;
import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.service.ITGoodsService;
import com.example.seckillmy.service.ITOrderService;
import com.example.seckillmy.service.ITSeckillOrderService;
import com.example.seckillmy.utils.JsonUtil;
import com.example.seckillmy.vo.GoodsVo;
import com.example.seckillmy.vo.RespBean;
import com.example.seckillmy.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private ITGoodsService itGoodsServicel;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ITOrderService itOrderService;


    /**
     * 下单操作
     *
     * @param
     * @return void
     * @author LiChao
     * @operation add
     * @date 6:48 下午 2022/3/8
     **/
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("接收消息：" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        TUser user = seckillMessage.getTUser();
        //判断是否超卖； 后面lua脚本代替了
        GoodsVo goodsVo = itGoodsServicel.findGoodsVobyGoodsId(goodsId);
//        Integer goodsVoNum = (Integer) redisTemplate.opsForValue().get("seckillGoods:" + goodsId);
//        if (goodsVoNum.intValue() < 1) {
//            return;
//        }
//
//        //判断是否重复抢购
//        TSeckillOrder tSeckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
//        if (tSeckillOrder != null) {
//            return;
//        }
        //下单操作
        try {
            itOrderService.secKill(user, goodsVo);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


//    @RabbitListener(queues = "queue")
//    public void receive(Object msg) {
//        System.out.println("接收到的消息" + msg);
//    }
//
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg) {
//        log.info("QUEUE01接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg) {
//        log.info("QUEUE02接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg) {
//        log.info("QUEUE01接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg) {
//        log.info("QUEUE02接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void receive05(Object msg) {
//        log.info("QUEUE01接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receive06(Object msg) {
//        log.info("QUEUE02接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_header01")
//    public void receive07(Message message) {
//        log.info("QUEUE01接收消息 message对象" + message);
//        log.info("QUEUE01接收消息" + new String(message.getBody()));
//    }
//
//    @RabbitListener(queues = "queue_header02")
//    public void receive08(Message message) {
//        log.info("QUEUE02接收消息 message对象" + message);
//        log.info("QUEUE02接收消息" + new String(message.getBody()));
//    }

}
