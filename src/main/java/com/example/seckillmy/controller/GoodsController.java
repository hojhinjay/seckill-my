package com.example.seckillmy.controller;

import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.service.ITGoodsService;
import com.example.seckillmy.service.ITUserService;
//import com.example.seckillmy.vo.DetailVo;
import com.example.seckillmy.service.RedissonBloomFilterService;
import com.example.seckillmy.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("goods")
@Api(value = "商品", tags = "商品")
public class GoodsController {

    @Autowired
    private ITUserService itUserService;
    @Autowired
    private ITGoodsService itGoodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    private RedissonBloomFilterService redissonBloomService;

//    @ApiOperation("商品列表")
//    @RequestMapping(value = "/toList2", produces = "text/html;charset=utf-8", method = RequestMethod.GET)
//    @ResponseBody
//    public String toList2(Model model, TUser user, HttpServletRequest request, HttpServletResponse response) {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        String html = (String) valueOperations.get("goodsList");
//        if (!StringUtils.isEmpty(html)) {
//            return html;
//        }
//
//        model.addAttribute("user", user);
//        model.addAttribute("goodsList", itGoodsService.findGoodsVo());
//
//        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
//        if (!StringUtils.isEmpty(html)) {
//            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
//        }
//        return html;
//    }
//
//    @ApiOperation("商品详情")
//    @RequestMapping(value = "/goodsDetail2/{goodsId}", produces = "text/html;charset=utf-8", method = RequestMethod.GET)
//    @ResponseBody
//    public String toDetail2(Model model, TUser user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response) {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
//        if (!StringUtils.isEmpty(html)) {
//            return html;
//        }
//
//        model.addAttribute("user", user);
//        GoodsVo goodsVo = itGoodsService.findGoodsVobyGoodsId(goodsId);
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date nowDate = new Date();
//        //秒杀状态
//        int seckillStatus = 0;
//        //秒杀倒计时
//        int remainSeconds = 0;
//
//        if (nowDate.before(startDate)) {
//            //秒杀还未开始0
//            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
//        } else if (nowDate.after(endDate)) {
//            //秒杀已经结束
//            seckillStatus = 2;
//            remainSeconds = -1;
//        } else {
//            //秒杀进行中
//            seckillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("remainSeconds", remainSeconds);
//        model.addAttribute("goods", goodsVo);
//        model.addAttribute("seckillStatus", seckillStatus);
//
//        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
//        if (!StringUtils.isEmpty(html)) {
//            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
//        }
//
//        return html;
//    }


    @ApiOperation("商品列表")
    @RequestMapping(value = "/toList",  method = RequestMethod.GET)
//    @ResponseBody
    public String toList(Model model,  TUser user, HttpServletRequest request, HttpServletResponse response) {

        model.addAttribute("user", user);
        model.addAttribute("goodsList", itGoodsService.findGoodsVo());
        return "goodsList";
    }
//    @ApiOperation("商品列表")
//    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8", method = RequestMethod.GET)
//    @ResponseBody
//    public String toList(Model model,  TUser user, HttpServletRequest request, HttpServletResponse response) {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        String html = (String) valueOperations.get("goodsList");
//        if (!StringUtils.isEmpty(html)) {
//            return html;
//        }
//
//        model.addAttribute("user", user);
//        model.addAttribute("goodsList", itGoodsService.findGoodsVo());
//
//        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
//        if (!StringUtils.isEmpty(html)) {
//            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
//        }
//        return html;
//    }

//    @ApiOperation("商品详情")   这个是从list页面跳转接口，接口返回页面与数据
//    @RequestMapping(value = "/toDetail/{goodsId}", method = RequestMethod.GET)
////    @ResponseBody
//    public String toDetail(Model model,TUser user, @PathVariable Long goodsId) {
//        GoodsVo goodsVo = itGoodsService.findGoodsVobyGoodsId(goodsId);
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date nowDate = new Date();
//        //秒杀状态
//        int seckillStatus = 0;
//        //秒杀倒计时
//        int remainSeconds = 0;
//
//        if (nowDate.before(startDate)) {
//            //秒杀还未开始0
//            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
//        } else if (nowDate.after(endDate)) {
//            //秒杀已经结束
//            seckillStatus = 2;
//            remainSeconds = -1;
//        } else {
//            //秒杀进行中
//            seckillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("user",user);
//        model.addAttribute("remainSeconds",remainSeconds);
//        model.addAttribute("seckillStatus",seckillStatus);
//        model.addAttribute("goods",itGoodsService.findGoodsVobyGoodsId(goodsId));
//       return "goodsDetail";
//    }

    @ApiOperation("更新商品详情sckill_goods") //这里是list前端跳转detail前端页面，跳转之后前端发送请求；
    @RequestMapping(value = "/update",method = RequestMethod.PATCH)
    @ResponseBody
    public RespBean updateDetail(@RequestBody updateGoods updategoods) {
        if (itGoodsService.updateGoodsMessage(updategoods).equals(new Integer(1))) {
            return RespBean.success();
        } else {
            return RespBean.updatefail();
        }
    }


    @ApiOperation("商品详情") //这里是list前端跳转detail前端页面，跳转之后前端发送请求；
    @RequestMapping(value = "/detail/{goodsId}", method = RequestMethod.GET)
    @ResponseBody
    public RespBean toDetail(TUser user, @PathVariable Long goodsId) {
        String key = "cache:shop:"+goodsId;
        Object goodsDetailMessage =  redisTemplate.opsForValue().get(key);
        //方案1:null与""都是object ==null是判断地址是否为null，这里是为了配合设置NULL的缓存穿透；
          if(goodsDetailMessage == null){
              GoodsVo goodsVo = itGoodsService.findGoodsVobyGoodsId(goodsId);//如果库中没有就会抛出异常了；
              Date startDate = goodsVo.getStartDate();
              Date endDate = goodsVo.getEndDate();
              Date nowDate = new Date();
              //秒杀状态
              int seckillStatus = 0;
              //秒杀倒计时
              int remainSeconds = 0;

              if (nowDate.before(startDate)) {
                  //秒杀还未开始0
                  remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
              } else if (nowDate.after(endDate)) {
                  //秒杀已经结束
                  seckillStatus = 2;
                  remainSeconds = -1;
              } else {
                  //秒杀进行中
                  seckillStatus = 1;
                  remainSeconds = 0;
              }
              DetailVo detailVo = new DetailVo();
              detailVo.setTUser(user);
              detailVo.setGoodsVo(goodsVo);
              detailVo.setRemainSeconds(remainSeconds);
              detailVo.setSecKillStatus(seckillStatus);
              return RespBean.success(detailVo);

          } else if ( goodsDetailMessage.equals("")) {
              return RespBean.error(RespBeanEnum.EMPTY_MESS);

          } else {
              Date startDate = ((GoodsVo) goodsDetailMessage).getStartDate();
              Date endDate = ((GoodsVo) goodsDetailMessage).getEndDate();
              Date nowDate = new Date();
              //秒杀状态
              int seckillStatus = 0;
              //秒杀倒计时
              int remainSeconds = 0;

              if (nowDate.before(startDate)) {
                  //秒杀还未开始0
                  remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
              } else if (nowDate.after(endDate)) {
                  //秒杀已经结束
                  seckillStatus = 2;
                  remainSeconds = -1;
              } else {
                  //秒杀进行中
                  seckillStatus = 1;
                  remainSeconds = 0;
              }
              DetailVo detailVo = new DetailVo();
              detailVo.setTUser(user);
              detailVo.setGoodsVo((GoodsVo) goodsDetailMessage);
              detailVo.setRemainSeconds(remainSeconds);
              detailVo.setSecKillStatus(seckillStatus);
              return RespBean.success(detailVo);

          }

    }





    @ApiOperation("商品详情") //这里是list前端跳转detail前端页面，跳转之后前端发送请求；
    @RequestMapping(value = "/detail1/{goodsId}", method = RequestMethod.GET)
    @ResponseBody
    public RespBean toDetail1(TUser user, @PathVariable Long goodsId) {

        String key = "cache:shop:" + goodsId;  //用在redis中，布隆中直接是goodsid
        //方案2:判断商品ID是否在布隆过滤中，如果在就可以去redis中查找；
        if (redissonBloomService.containsValue("bloomkey1", String.valueOf(goodsId))) {
            Object goodsDetailMessage = redisTemplate.opsForValue().get(key);

            if(goodsDetailMessage == null){//如果误判了，那么本来没有的数据以为redis中有
                GoodsVo goodsVo = itGoodsService.findGoodsVobyGoodsId(goodsId);//如果库中没有就会抛出异常了；
                Date startDate = goodsVo.getStartDate();
                Date endDate = goodsVo.getEndDate();
                Date nowDate = new Date();
                //秒杀状态
                int seckillStatus = 0;
                //秒杀倒计时
                int remainSeconds = 0;

                if (nowDate.before(startDate)) {
                    //秒杀还未开始0
                    remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
                } else if (nowDate.after(endDate)) {
                    //秒杀已经结束
                    seckillStatus = 2;
                    remainSeconds = -1;
                } else {
                    //秒杀进行中
                    seckillStatus = 1;
                    remainSeconds = 0;
                }
                DetailVo detailVo = new DetailVo();
                detailVo.setTUser(user);
                detailVo.setGoodsVo(goodsVo);
                detailVo.setRemainSeconds(remainSeconds);
                detailVo.setSecKillStatus(seckillStatus);
                return RespBean.success(detailVo);

            }else {//如果redis有就直接返回
            Date startDate = ((GoodsVo) goodsDetailMessage).getStartDate();
            Date endDate = ((GoodsVo) goodsDetailMessage).getEndDate();
            Date nowDate = new Date();
            //秒杀状态
            int seckillStatus = 0;
            //秒杀倒计时
            int remainSeconds = 0;

            if (nowDate.before(startDate)) {
                //秒杀还未开始0
                remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
            } else if (nowDate.after(endDate)) {
                //秒杀已经结束
                seckillStatus = 2;
                remainSeconds = -1;
            } else {
                //秒杀进行中
                seckillStatus = 1;
                remainSeconds = 0;
            }
            DetailVo detailVo = new DetailVo();
            detailVo.setTUser(user);
            detailVo.setGoodsVo((GoodsVo) goodsDetailMessage);
            detailVo.setRemainSeconds(remainSeconds);
            detailVo.setSecKillStatus(seckillStatus);
            return RespBean.success(detailVo);
        } }
        else  {
            //如果不在布隆过滤器中，那么就不去redis中找了，直接返回null，如果布隆过滤器判断一个元素不在一个集合中，那这个元素一定不会再集合中
            return RespBean.error(RespBeanEnum.EMPTY_MESS);
        }
    }
}

