package com.education.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfig {

    /**
     * 配置Swagger文档信息
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30) // 兼容OpenAPI 3.0
                .apiInfo(apiInfo())
                .securityContexts(Collections.singletonList(securityContext())) // 配置安全上下文
                .securitySchemes(Collections.singletonList(apiKey())) // 配置API密钥
                .select()
                // 指定扫描的控制器包路径（替换为你的实际包名）
                .apis(RequestHandlerSelectors.basePackage("com.education.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 文档基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "教育平台API文档",
                "帖子、用户、关注等接口文档",
                "1.0.0",
                null,
                new Contact("联系人", "联系网址", "联系邮箱"),
                null,
                null,
                Collections.emptyList()
        );
    }

    /**
     * 配置API密钥（token请求头）
     */
    private ApiKey apiKey() {
        // 参数1：名称（自定义）；参数2：请求头key（与你的JWT配置中的tokenName一致，如"token"）；参数3：位置（header）
        return new ApiKey("token", "token", "header");
    }

    /**
     * 配置安全上下文，指定哪些接口需要携带token
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth()) // 关联安全引用
                .operationSelector(operationContext -> true) // 所有接口都生效
                .build();
    }

    /**
     * 安全引用配置
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(
                new SecurityReference("token", authorizationScopes)
        );
    }
}