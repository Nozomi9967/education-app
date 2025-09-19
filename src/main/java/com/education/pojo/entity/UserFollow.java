package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户关注关系实体类")
@TableName("edu_follow")
public class UserFollow implements Serializable {
    private String followerId;
    private String followedId;
    private LocalDateTime createTime;
}
