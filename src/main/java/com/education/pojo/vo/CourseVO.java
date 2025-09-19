package com.education.pojo.vo;

import com.education.pojo.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课程展示VO")
public class CourseVO extends Course {

    @Schema(description = "创建者昵称")
    private String creatorNickname;

    @Schema(description = "兴趣圈名称")
    private String interestName;
}
