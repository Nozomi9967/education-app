package com.education.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

// 更新活动请求DTO
@Data
public class UpdateActivityRequest {
    @NotNull(message = "活动ID不能为空")
    private Long id;

    private Integer interestId;

    @Size(max = 50, message = "活动名长度不能超过50字符")
    private String name;

    private String cover;
    private String theme;
    private String highlight;

    @Size(max = 100, message = "活动时间长度不能超过100字符")
    private String time;

    @Size(max = 200, message = "活动地点长度不能超过200字符")
    private String location;

    private String participateMethod;
    private String eventFlow;
}