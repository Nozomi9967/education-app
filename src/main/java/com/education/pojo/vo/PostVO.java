package com.education.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "返回给前端的帖子信息类")
public class PostVO implements Serializable {
    private Long id;
    private String nickname;
    private String avatar;
    private String location;
    private String name;
    private String title;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private Integer forwardCount;
    private Integer status;
    private LocalDateTime createTime;
    private Integer isFollowed;
}
