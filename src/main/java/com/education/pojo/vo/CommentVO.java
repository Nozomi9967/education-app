package com.education.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "返回给前端的评论信息类")
public class CommentVO implements Serializable {
    private Long id;
    private String userId;
    private String content;
    private Integer likeCount;
    private LocalDateTime createTime;
    private Integer isFollowed;

}
