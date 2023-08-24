package com.example.seckillmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckillmy.entity.securityUser;
import com.example.seckillmy.exception.security.SysMenuRoleDTO;

import java.util.List;

public interface roleMenuMapper extends BaseMapper<SysMenuRoleDTO> {

    public List<SysMenuRoleDTO> getAllMenuRole();
}