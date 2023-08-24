package com.example.seckillmy.exception.security;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWTUtil;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //不区分大小写
        String token = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);

        //startWith():是否以指定字符串开头
        if(StrUtil.isNotBlank(token) && StrUtil.startWith(token, SecurityConstants.TOKEN_PREFIX)){
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");


            String userid = String.valueOf(TokenUtils.getUserIdByToken(token));

            if(StrUtil.isNotBlank(userid)){//三参username是isauthenticated为true,anonymous的isauthenticated为true但是没用
                UserDetails userDetails = userDetailsService.loadUserByUsername(userid);
                if(userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        chain.doFilter(request, response);//没有token直接放行；有则设置到context中再放行；
    }

}
