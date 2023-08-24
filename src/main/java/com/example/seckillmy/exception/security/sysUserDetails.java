package com.example.seckillmy.exception.security;

import com.example.seckillmy.entity.TUser;
import com.example.seckillmy.entity.securityUser;
import com.example.seckillmy.entity.sysUserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class sysUserDetails implements UserDetails {


    private Long id;
    private String username;
    private String password;
    private Integer status;

    private String salt;

    /** 头像 **/
    private String head;

    /** 注册时间 **/
    private Date registerDate;

    /** 最后一次登录事件 **/
    private Date lastLoginDate;

    /** 登录次数 **/
    private Integer loginCount;



    private List<String> roles;



    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(String -> new SimpleGrantedAuthority(String))
                .collect(Collectors.toList());
    }

    public sysUserDetails(securityUser securityUser, ArrayList<String> roles) {
        this.id = securityUser.getId();
        this.username =securityUser.getUserName();
        this.password = securityUser.getPassword();
        this.roles = roles;
    }



    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true ;
    }

}
