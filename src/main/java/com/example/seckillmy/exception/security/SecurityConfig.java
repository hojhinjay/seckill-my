package com.example.seckillmy.exception.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.Map;
//@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Autowired(required = false)
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @Autowired
    private NotAuthUrls notAuthUrls;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        for (String notAuthUrl : notAuthUrls.getNotAuthUrls()){
            registry.antMatchers(notAuthUrl).permitAll(); //antMatchers（）.authenticated表示认证才能访问  anonymous（）匿名访问,已认证不可以访问
        }
        registry.antMatchers(HttpMethod.OPTIONS).permitAll();

//        http.formLogin()
        registry
//                .and()
//                .authorizeRequests().antMatchers("/login/notlogin").hasAnyAuthority("2")  改用动态
                .anyRequest()
                .authenticated()
                .and().csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //不创建session
                .and()
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


//设置dynamicSecurityMetadataSource  order1先创建
        if(dynamicSecurityMetadataSource != null){
            registry.and().addFilterBefore(dynamicSecurityFilter(), FilterSecurityInterceptor.class);
        }
    }

    @Bean("BCryptPasswordEncoder")
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
//
//    @Bean
//    public JwtAccessDeniedHandler jwtAccessDeniedHandler(){
//        return new JwtAccessDeniedHandler();
//    }
//
//    @Bean
//    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint(){
//        return new JwtAuthenticationEntryPoint();
//    }
//
//    @Bean
//    public JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter(){
//        return new JwtTokenAuthenticationFilter();
//    }
//
    @Bean
    public DynamicSecurityFilter dynamicSecurityFilter(){
        return new DynamicSecurityFilter();
    }

//    @Order(1)
//    @Bean("dynamicSecurityMetadataSource")
//    @ConditionalOnBean(name = "dynamicSecurityService")
//    public DynamicSecurityMetadataSource dynamicSecurityMetadataSource(){
//        return new DynamicSecurityMetadataSource();
//    }

    @Bean
    public DynamicAccessDecisionManager dynamicAccessDecisionManager(){
        return new DynamicAccessDecisionManager();
    }

}
