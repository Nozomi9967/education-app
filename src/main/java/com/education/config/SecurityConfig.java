package com.education.config;

import com.education.interceptor.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.education.properties.JwtProperties;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProperties jwtProperties;  // 注意：@Autowired 在这里多余，构造函数注入无需添加

    // 注入JWT配置属性
    public SecurityConfig(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 放行Swagger相关路径
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
                .permitAll()
                // 放行注册登录接口（添加此行）
                .antMatchers("/auth/**")
                .permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated();

        // 禁用CSRF（适用于API场景）
        http.csrf().disable();

        // 添加JWT过滤器到Security过滤链中，在用户名密码认证过滤器之前执行
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtProperties),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}