package com.example.seckillmy.exception.security;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckillmy.mapper.roleMenuMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//SysMenuRoleDTO 随便写的，只需要执行自己的SQL
public class sysMenuServiceImpl extends ServiceImpl<roleMenuMapper, SysMenuRoleDTO> implements sysMenuService {

    @Override
    public List<SysMenuRoleDTO> getAllMenuRole() {
        return this.getBaseMapper().getAllMenuRole();
    }
}
