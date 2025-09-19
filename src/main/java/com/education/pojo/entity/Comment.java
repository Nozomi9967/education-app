package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "评论类")
@TableName("edu_post_comment")
public class Comment implements Serializable {
    private Long id;
    private String userId;
    private Long postId;
    private String content;
    private Integer likeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}
