package com.education.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用于展示用户参与的课程信息")
public class CourseJoinVO implements Serializable {
    @Schema(description = "课程ID")
    private Long id;
    @Schema(description = "课程名称")
    private String name;
    @Schema(description = "课程封面")
    private String cover;
    @Schema(description = "课程章节标题")
    private String chapTitle;
    @Schema(description = "课程课时排序号")
    private Integer lessonSort;
    @Schema(description = "当前学到的课时")
    private String curLesson;
    @Schema(description = "课程进度")
    private Integer progress;
}
