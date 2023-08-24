package com.example.seckillmy.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_user_role")
@ApiModel(value = "用户表", description = "用户表")
public class sysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;

    private String userId;

    private String roleId;


}
