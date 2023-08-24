package com.example.seckillmy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.entity.securityUser;
import com.example.seckillmy.vo.LoginVo;
import com.example.seckillmy.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface ITUserService extends IService<TUser> {





    /**
     * 登录方法
     *
     * @param loginVo
     * @param request
     * @param response
     * @return com.example.seckilldemo.vo.RespBean
     * @author LC
     * @operation add
     * @date 1:49 下午 2022/3/3
     **/
    RespBean doLongin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     *
     * @param userTicket
     * @return com.example.seckilldemo.entity.TUser
     * @author LC
     * @operation add
     * @date 1:50 下午 2022/3/3
     **/
    TUser getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);


    /**
     * 更新密码
     *
     * @param userTicket
     * @param
     * @param password
     * @return com.example.seckilldemo.vo.RespBean
     * @author LC
     * @operation add
     * @date 9:05 下午 2022/3/4
     **/
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);

    securityUser getUser(String username);
}
