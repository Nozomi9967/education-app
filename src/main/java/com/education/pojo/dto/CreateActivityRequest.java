package com.education.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

// 创建活动请求DTO
@Data
public class CreateActivityRequest {

    @NotNull(message = "兴趣圈ID不能为空")
    private Integer interestId;

    @NotBlank(message = "活动名不能为空")
    @Size(max = 50, message = "活动名长度不能超过50字符")
    private String name;

    private String cover;

    @NotBlank(message = "活动主题不能为空")
    private String theme;

    private String highlight;

    @NotBlank(message = "活动时间不能为空")
    @Size(max = 100, message = "活动时间长度不能超过100字符")
    private String time;

    @NotBlank(message = "活动地点不能为空")
    @Size(max = 200, message = "活动地点长度不能超过200字符")
    private String location;

    @NotBlank(message = "参与方式不能为空")
    private String participateMethod;

    private String eventFlow;
}