package com.education.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Schema(description = "修改帖子类")
public class PostUpdateDTO implements Serializable {

    @Schema(description = "帖子id",example = "1")
    private Long id;

    @Size(max = 10, message = "标题不能超过10个字符")
    @Schema(description = "帖子标题（最多10个字符）", example = "我的第一篇帖子")
    private String title;

    @Size(max = 1000, message = "内容不能超过1000个字符")

    @Schema(description = "帖子内容（最多1000个字符）", example = "这是帖子的详细内容...")
    private String content;
}
