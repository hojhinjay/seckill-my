package com.example.seckillmy.exception.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenLogoutHandler implements LogoutHandler {

   private TokenUtils tokenUtils;
   private RedisTemplate redisTemplate;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
       String token = request.getHeader("token");
        String username = TokenUtils.getUsernameByToken(token);
        if(token!=null){
            redisTemplate.opsForValue().getAndDelete(username);
        }
    }

    public TokenLogoutHandler(TokenUtils tokenUtils, RedisTemplate redisTemplate) {
        this.tokenUtils = tokenUtils;
        this.redisTemplate = redisTemplate;
    }
}
