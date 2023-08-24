package com.example.seckillmy.controller;

import com.example.seckillmy.exception.security.TokenUtils;
import com.example.seckillmy.exception.security.sysUserDetails;
import com.example.seckillmy.service.ITUserService;
import com.example.seckillmy.vo.LoginVo;
import com.example.seckillmy.vo.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/login")
@Slf4j
@Api(value = "登录", tags = "登录")
public class LoginController {


    @Autowired
    private ITUserService tUserService;

    @Autowired
    private UserDetailsService UserDetailsServiceImpl;

    @ResponseBody
    public String logintest() {

        return "logintest";

    }


    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam String username) {
//        方便如果方法调用方法
        sysUserDetails sysUserDetails = (com.example.seckillmy.exception.security.sysUserDetails) UserDetailsServiceImpl.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(sysUserDetails, null, sysUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", sysUserDetails.getUsername());
        claims.put("userId", sysUserDetails.getId());
        String token = TokenUtils.createToken(claims);
        return token;

    }

    @RequestMapping(value = "/notlogin", method = {RequestMethod.POST})
    @ResponseBody
    public String notlogin() {

        return "notlogin";

    }

    /**
     * 跳转登录页面
     *
     * @param
     * @return java.lang.String
     * @author LC
     * @operation add
     * @date 10:13 上午 2022/3/2
     **/
    @ApiOperation("跳转登录页面")
    @RequestMapping(value = "/toLogin", method = {RequestMethod.GET})
    public String toLogin() {
        return "login";
    }


    @ApiOperation("登录接口")
    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doLogin(@Validated LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        log.info("{}", loginVo);
        return tUserService.doLongin(loginVo, request, response);
    }
}
