package com.example.seckillmy.exception.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class TokenUtils {

    public static String createToken(Map<String, Object> claims){
        return Jwts.builder()
                .setClaims(claims)
                // 生成token的过期时间
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRE_TIME * 1000))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET)
                .compact();
    }

    public static Claims parseToken(String token){

        return Jwts.parser()
                .setSigningKey(SecurityConstants.TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }


    public static String getUsernameByToken(String token){
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public static Long getUserIdByToken(String token){
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

}
