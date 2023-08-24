package com.example.seckillmy.exception.security;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private DynamicSecurityService dynamicSecurityService;

    private static Map<RequestMatcher, List<ConfigAttribute>> configAttributeMap = null;

    @PostConstruct
    public void loadDynamicSecuritySource(){
// 获取url ： 多个role
        configAttributeMap = dynamicSecurityService.loadDynamicSecuritySource();
    }

    public void clearDataSource() {
        configAttributeMap.clear();
        configAttributeMap = null;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if(configAttributeMap == null)
        {this.loadDynamicSecuritySource();}
        List<ConfigAttribute> configAttributeList = Lists.newArrayList();

        /** 获取当前访问路径，所以路径取出来，跟当前路径对比一样把value即权限就放入map中 */
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        Iterator<RequestMatcher> iterator = configAttributeMap.keySet().iterator();
        while (iterator.hasNext()){
            RequestMatcher matcher = iterator.next();
            if(matcher.matches(request)){
                configAttributeList.addAll(configAttributeMap.get(matcher));
            }
        }
        return configAttributeList;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
