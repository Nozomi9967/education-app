package com.education.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderUtil {

    // 初始化BCrypt加密器（Spring Security内置）
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 加密密码
    public String encodePassword(String rawPassword) {
        // BCrypt会自动生成随机盐值，每次加密结果不同（但可验证）
        return passwordEncoder.encode(rawPassword);
    }

    // 验证密码（原始密码 vs 加密后的密码）
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}