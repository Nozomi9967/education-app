package com.education.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.pojo.entity.Synonym;

import java.util.List;

public interface SynonymService extends IService<Synonym> {

    /**
     * 根据关键词查询启用状态的同义词
     */
    List<Synonym> selectByKeyword(String keyword);

    /**
     * 批量查询关键词的启用状态同义词
     */
    List<Synonym> selectByKeywords(List<String> keywords);

    /**
     * 根据关键词和学科查询同义词
     */
    List<Synonym> selectByKeywordAndSubject(String keyword, String subject);
}
