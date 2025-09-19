package com.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.mapper.SynonymMapper;
import com.education.pojo.entity.Synonym;
import com.education.service.SynonymService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SynonymServiceImpl extends ServiceImpl<SynonymMapper, Synonym> implements SynonymService {

    /**
     * 根据关键词查询启用状态的同义词
     * WHERE keyword = ? AND status = 1（假设1为启用状态）
     */
    @Override
    public List<Synonym> selectByKeyword(String keyword) {
        QueryWrapper<Synonym> queryWrapper = new QueryWrapper<>();
        // 条件：关键词匹配 + 启用状态（根据实际字段名调整）
        queryWrapper.eq("keyword", keyword)
                .eq("status", 1); // 假设status=1表示启用
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 批量查询关键词的启用状态同义词
     * WHERE keyword IN (?, ?, ...) AND status = 1
     */
    @Override
    public List<Synonym> selectByKeywords(List<String> keywords) {
        QueryWrapper<Synonym> queryWrapper = new QueryWrapper<>();
        // 条件：关键词在列表中 + 启用状态
        queryWrapper.in("keyword", keywords)
                .eq("status", 1);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据关键词和学科查询同义词
     * WHERE keyword = ? AND subject = ? AND status = 1
     */
    @Override
    public List<Synonym> selectByKeywordAndSubject(String keyword, String subject) {
        QueryWrapper<Synonym> queryWrapper = new QueryWrapper<>();
        // 条件：关键词匹配 + 学科匹配 + 启用状态
        queryWrapper.eq("keyword", keyword)
                .eq("subject", subject)
                .eq("status", 1);
        return baseMapper.selectList(queryWrapper);
    }
}
