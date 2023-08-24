package com.example.seckillmy.config;

import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.service.ITUserService;
import com.example.seckillmy.utils.CookieUtil;
import com.example.seckillmy.vo.RespBean;
import com.example.seckillmy.vo.RespBeanEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author: LC
 * @date 2022/3/9 4:27 下午
 * @ClassName: AccessLimitInterceptor
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private ITUserService itUserService;
    @Autowired
    private RedisTemplate redisTemplate;
    //接口限流，计数器算法
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            TUser tUser = getUser(request, response);//为了拼接请求url，登录之后用户就放入redis中了，根据cookie的id查询user；
            UserContext.setUser(tUser);//拦截器在userArgumentResolver之前，所以会放入threadlocal，所以后面请求需要user类，
            //也可以在threadlocal讲user传入方法的形参中；（security直接可以获取）
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            if (needLogin) {
                if (tUser == null) {
                    render(response, RespBeanEnum.SESSION_ERROR);
                }
                key += ":" + tUser.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);
            if (count == null) {
                valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            } else if (count < maxCount) {
                valueOperations.increment(key);
            } else {
                render(response, RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }


    private TUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        return itUserService.getUserByCookie(userTicket, request, response);
    }

    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter printWriter = response.getWriter();
        RespBean bean = RespBean.error(respBeanEnum);
        printWriter.write(new ObjectMapper().writeValueAsString(bean));
        printWriter.flush();
        printWriter.close();
    }

}
