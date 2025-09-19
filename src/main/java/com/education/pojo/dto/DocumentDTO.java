package com.education.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文档数据传输对象
 */
@Data
public class DocumentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 文档id
    private Long id;
    // 文档标题
    private String title;
    // 文档内容摘要
    private String contentSummary;
    // 文档创建时间
    private Date createTime;
    // 文档作者
    private String author;
    // 文档所属分类（如“数学”“语文”等）
    private String category;
    // 文档关联课程（若有）
    private String course;
    // 搜索匹配得分
    private Double score;
}