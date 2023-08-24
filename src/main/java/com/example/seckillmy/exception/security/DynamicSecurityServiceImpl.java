package com.example.seckillmy.exception.security;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DynamicSecurityServiceImpl implements DynamicSecurityService {


    @Autowired
    private sysMenuService sysMenuServiceImpl; //取出所有路径：多个角色


    @Override
    public Map<RequestMatcher, List<ConfigAttribute>> loadDynamicSecuritySource() {
//采用三更的数据库脚本就行  表的名字都一样；  {AntPathRequestMatcher@8623} "Ant [pattern='/login/notlogin']" -> {ArrayList@8624}  size = 1
        Map<RequestMatcher, List<ConfigAttribute>> map = Maps.newConcurrentMap();
        List<SysMenuRoleDTO> sysMenuRoleDTOList = sysMenuServiceImpl.getAllMenuRole();
        for (SysMenuRoleDTO sysMenuRoleDTO : sysMenuRoleDTOList) {
            map.put(new AntPathRequestMatcher(sysMenuRoleDTO.getPath()),
                    /** 所有权限对应的角色 */
                    sysMenuRoleDTO.getSysRoleDTOList()
                            .stream()
                            .map(sysRoleDTO -> new org.springframework.security.access.SecurityConfig(String.valueOf(sysRoleDTO.getId())))
                            .collect(Collectors.toList())
            );
        }
        return map;

    }
}