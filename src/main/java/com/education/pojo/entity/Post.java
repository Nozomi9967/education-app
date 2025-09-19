package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Schema(description = "帖子类")
@TableName("edu_post")
public class Post implements Serializable {
    private Long id;
    private String userId;
    private Integer interestId;
    private String title;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private Integer forwardCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}
