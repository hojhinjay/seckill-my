package com.example.seckillmy.exception.security;

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


public class DefaultpassWordEncoder implements PasswordEncoder {
// hutool加密，验证时候无法Match   BCryptPasswordEncoder
    @Autowired
    private static PasswordEncoder BCryptPasswordEncoder;
    @Override
    public  String encode(CharSequence rawPassword) {

        return BCrypt.hashpw(rawPassword.toString());

    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword.toString(),encodedPassword);//第一个参数是plaintext 第二个是encode加密后的
    }

}
