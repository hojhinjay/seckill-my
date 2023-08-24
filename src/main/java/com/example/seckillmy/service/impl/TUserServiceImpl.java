package com.example.seckillmy.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.entity.securityUser;
import com.example.seckillmy.exception.GlobalException;
import com.example.seckillmy.mapper.TUserMapper;
import com.example.seckillmy.service.ITUserService;
import com.example.seckillmy.utils.CookieUtil;
import com.example.seckillmy.utils.MD5Util;
import com.example.seckillmy.utils.UUIDUtil;
import com.example.seckillmy.vo.LoginVo;
import com.example.seckillmy.vo.RespBean;
import com.example.seckillmy.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Primary
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements ITUserService {


//    @Autowired
//    private TUserMapper tUserMapper;

    @Autowired
    private RedisTemplate redisTemplate;

   @Autowired
   private com.example.seckillmy.mapper.securityUserMapper securityUserMapper;

    public securityUser getUser(String username){
        return  securityUserMapper.selectById(username);
    }


    public RespBean doLongin1(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(mobile,password);
//        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
return null;
    }
    @Override
    public RespBean doLongin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
////        参数校验  放入自定义注解了
//        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
//            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
//        }
////        TODO 因为我懒测试的时候，把手机号码和密码长度校验去掉了，可以打开。页面和实体类我也注释了，记得打开
//        if (!ValidatorUtil.isMobile(mobile)) {
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        TUser user = this.getBaseMapper().selectById(mobile);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
//        System.out.println(MD5Util.formPassToDBPass(password, user.getSalt()));
        //判断密码是否正确
        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
//        //生成Cookie
        String userTicket = UUIDUtil.uuid();
//        //将用户信息存入redis
        redisTemplate.opsForValue().set("user:" + userTicket, user);

//        request.getSession().setAttribute(userTicket, user);
        CookieUtil.setCookie(request, response, "userTicket", userTicket);
        return RespBean.success(1);

    }

    @Override
    public TUser getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        TUser user = (TUser) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
       return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        TUser user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int result = this.getBaseMapper().updateById(user);
        if (1 == result) {
            //跟新密码之后，跟新user之后，删除Redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }


}
