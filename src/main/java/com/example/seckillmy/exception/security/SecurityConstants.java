package com.example.seckillmy.exception.security;

public final class  SecurityConstants {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String TOKEN_SECRET ="123456";
    public static final long TOKEN_EXPIRE_TIME = 60 * 60 * 2;// token有效期(秒)
}
