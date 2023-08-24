package com.example.seckillmy.config;

import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.service.ITUserService;
import com.example.seckillmy.service.ITUserService;
import com.example.seckillmy.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义用户参数
 *
 * @author: LC
 * @date 2022/3/3 4:46 下午
 * @ClassName: UserArgumentResolver
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private ITUserService itUserService;

    @Override   //public RespBean doLogin(@Validated LoginVo loginVo, HttpServletRequest request, HttpServletResponse response)
    public boolean supportsParameter(MethodParameter parameter) { //如果请求里面带有user类的话
        Class<?> parameterType = parameter.getParameterType(); //TUser
        return parameterType == TUser.class;   //返回true才继续执行下面的方法；
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
      //因为每次请求都只默认带上cookie，从而需要从redis取出user
        return UserContext.getUser();
       // return UserContext.getUser();
        //从自动带上的cookie中获取userTicket
//        HttpServletRequest nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse nativeResponse = webRequest.getNativeResponse(HttpServletResponse.class);
//        String userTicket = CookieUtil.getCookieValue(nativeRequest, "userTicket");
//        if (StringUtils.isEmpty(userTicket)) {
//            return null;
//        }
//        return itUserService.getUserByCookie(userTicket, nativeRequest, nativeResponse);//从redis中获取User传到controller中去
    }

}
