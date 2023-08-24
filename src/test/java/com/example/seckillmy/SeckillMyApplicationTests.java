package com.example.seckillmy;

import com.example.seckillmy.exception.security.TokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SeckillMyApplicationTests {

    @Autowired
    private PasswordEncoder BCryptPasswordEncoder;



    @Test
    public void contextLoads() {
        System.out.println(BCryptPasswordEncoder.encode("1"));
        System.out.println(BCryptPasswordEncoder.matches("1",BCryptPasswordEncoder .encode("1")));

        System.out.println(TokenUtils.getUserIdByToken("Bearer eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2OTI4MTM3MzIsInVzZXJJZCI6MSwidXNlcm5hbWUiOiJoamoifQ.30JWIlNEPk2Y29i0jxuPzQ1_D5ymwlvZnQMPPdgBvJxKGqjVNUTFf2UIf9Ehr6mVMdszO30wmaZNVFgyqTumsg"));

    }

}
