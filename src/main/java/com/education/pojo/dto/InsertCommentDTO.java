package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Schema(description = "提供给前端传评论数据的类")
public class InsertCommentDTO implements Serializable {

    @NotNull(message = "帖子ID不能为空")
    @Schema(description = "帖子ID", example = "17")
    private Long postId;

    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 200, message = "评论内容长度必须在1-200个字符之间")
    @Schema(description = "评论内容", example = "这篇帖子很有价值！")
    private String content;
}
