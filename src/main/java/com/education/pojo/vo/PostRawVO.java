package com.education.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "用于分页展示帖子的类")
public class PostRawVO {
    private Long id;
    private Integer isFollowed;
    private String content;
    private List<String> images;
    private Integer likeCount;
    private Integer commentCount;
    private Integer forwardCount;
    private String nickname;
    private String avatar;
    private String location;
    private LocalDateTime createTime;
}
