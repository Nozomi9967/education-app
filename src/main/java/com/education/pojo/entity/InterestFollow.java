package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "兴趣圈关注类")
@TableName("edu_interest_follow")
public class InterestFollow implements Serializable {
    private Integer interestId;
    private String userId;
    private LocalDateTime createTime;
}
