package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户订阅课程的关系")
@TableName("edu_user_course")
public class UserCourse implements Serializable {
    private Long courseId;
    private String userId;
    private LocalDateTime createTime;
}
