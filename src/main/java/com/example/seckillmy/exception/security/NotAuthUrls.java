package com.example.seckillmy.exception.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;


@Data
@Component
@ConfigurationProperties(prefix = "security")
public class NotAuthUrls {

    private LinkedHashSet<String> notAuthUrls;

    public LinkedHashSet<String> getNotAuthUrls() {
        return notAuthUrls;
    }

    public void setNotAuthUrls(LinkedHashSet<String> notAuthUrls) {
        this.notAuthUrls = notAuthUrls;
    }
}
