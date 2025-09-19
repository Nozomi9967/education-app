package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;



@Data
@Schema(description = "评论内容更新类")
public class CommentUpdateDTO implements Serializable {

    @NotNull(message = "评论ID不能为空")
    @Schema(description = "评论ID", example = "1967606211852091394")
    private Long id;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 200, message = "评论内容不能超过200个字符")
    @Schema(description = "评论内容", example = "这是一条更新后的评论内容", maxLength = 200)
    private String content;
}
