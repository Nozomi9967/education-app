package com.education.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

// 查询活动请求DTO
@Data
public class ActivityQueryRequest {
    private String userId;
    private Integer interestId;
    private String name;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // 分页参数
    private Integer current = 1;
    private Integer size = 10;
}