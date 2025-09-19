package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "帖子转发实体类")
@TableName("edu_user_forward")
public class UserForward implements java.io.Serializable {
    private  Long id;
    private String userId;
    private Long originalPostId;
    private Long forwardPostId;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime deleteTime;
}
