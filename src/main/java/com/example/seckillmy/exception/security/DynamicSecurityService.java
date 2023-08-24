package com.example.seckillmy.exception.security;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Map;

/**
 * @Auther Yeh Feng
 * @Date 2022-10-22
 * @Description 动态权限相关业务类
 */
public interface DynamicSecurityService {

    /**
     * key:   匹配器(在DynamicSecurityMetadataSource起作用)
     * value: 资源所对应的角色
     * @return
     */
    Map<RequestMatcher, List<ConfigAttribute>> loadDynamicSecuritySource();
}
