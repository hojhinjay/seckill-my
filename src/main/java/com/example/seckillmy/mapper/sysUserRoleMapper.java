package com.example.seckillmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckillmy.entity.securityUser;
import com.example.seckillmy.entity.sysUserRole;

import java.util.ArrayList;
import java.util.List;

public interface sysUserRoleMapper extends BaseMapper<sysUserRole> {
    /** 根据用户ID获取角色 */
    ArrayList<sysUserRole> getRolesByUserId(String userId);
}
