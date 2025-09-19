package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Schema(description = "用户类")
@TableName("edu_user")
public class User {
    private String id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String avatar;
    private Integer gender;
    private String location;
    private LocalDateTime birthday;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}
