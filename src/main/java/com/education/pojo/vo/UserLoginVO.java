package com.education.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "向前端传递登录成功后的用户信息")
public class UserLoginVO {
    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String token;
}
