package com.example.seckillmy.exception.security;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class SysMenuRoleDTO {
// url与它所对应的role list
    private Long id;
    private String menuName;
    private String path;
    private Date gmtCreate;
    private Date gmtModified;
    private List<SysRoleDTO> sysRoleDTOList;
}
