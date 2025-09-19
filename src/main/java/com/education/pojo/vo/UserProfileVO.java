package com.education.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Schema(description = "返回给前端的用户个人简介类")
public class UserProfileVO implements Serializable {
    private String id;
    private String username;
    private String nickname;
    private String phone;
    private String avatar;
    private Integer gender;
    private String location;
    private LocalDateTime birthday;
}
