package com.education;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@MapperScan("com.education.mapper") // Ensure this matches your mapper package
@EnableSwagger2
public class EducationAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(EducationAppApplication.class, args);
    }

}