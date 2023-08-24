package com.example.seckillmy.exception.security;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckillmy.entity.TSeckillGoods;

import java.util.List;

public interface sysMenuService extends IService<SysMenuRoleDTO> {
    List<SysMenuRoleDTO> getAllMenuRole();
}
