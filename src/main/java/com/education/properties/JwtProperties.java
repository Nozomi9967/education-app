package com.education.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "edu.jwt")
@Data
public class JwtProperties {

    private String secretKey;// 密钥
    private long ttl;// 有效时长
    private String tokenName;// token名
}
