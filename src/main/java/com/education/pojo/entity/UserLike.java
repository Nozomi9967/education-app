package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户帖子点赞实体类")
@TableName("edu_user_like")
public class UserLike implements Serializable {
    private Long postId;
    private String userId;
    private LocalDateTime createTime;
}
