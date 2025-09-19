package com.education.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "搜索历史实体类")
@TableName("edu_search_history")
public class SearchHistory implements Serializable {
    private Long id;
    private String userId;
    private String keyword;
    private LocalDateTime createTime;
}
