package com.education.interceptor;

import com.education.context.BaseContext;
import com.education.properties.JwtProperties;
import com.education.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器：拦截请求并解析令牌
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1. 从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getTokenName());

        // 2. 验证令牌并解析用户ID
        if (token != null && !token.isEmpty()) {
            try {
                // 解析令牌获取claims
                Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);

                // 从claims中获取用户ID（假设生成令牌时使用"userId"作为键）
                String userId = claims.get("userId", String.class);

                if (userId != null) {
                    // 3. 将用户ID存入线程上下文
                    BaseContext.setCurrentId(userId);

                    // 4. 设置Spring Security上下文（表示已认证）
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 令牌无效或过期，清空线程上下文和安全上下文
                BaseContext.removeCurrentId();
                SecurityContextHolder.clearContext();
                // 可以根据需要返回401错误
                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效的令牌");
                // return;
            }
        }

        // 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }
}
