package com.example.seckillmy.exception.security;

import com.example.seckillmy.entity.sysUserRole;
import com.example.seckillmy.mapper.sysUserRoleMapper;
import com.example.seckillmy.service.ITUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class userDetailServiceImpl implements UserDetailsService {

    @Autowired
    private ITUserService tUserService;

    @Autowired
    private com.example.seckillmy.mapper.sysUserRoleMapper sysUserRoleMapper;

    @Override
    public sysUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//username实际上是id
        ArrayList<sysUserRole> sysUserRoles = sysUserRoleMapper.getRolesByUserId(username);
        ArrayList<String> roleList = (ArrayList<String>) sysUserRoles.stream().map(sysUserRole-> sysUserRole.getRoleId()).collect(Collectors.toList());
        return new sysUserDetails(tUserService.getUser(username),roleList);
    }
}
