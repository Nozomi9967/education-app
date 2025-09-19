package com.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.education.constant.BusinessConstant;
import com.education.context.BaseContext;
import com.education.mapper.SearchHistoryMapper;
import com.education.pojo.entity.SearchHistory;
import com.education.pojo.vo.PostVO;
import com.education.pojo.vo.SearchPersonalInfoVO;
import com.education.result.Result;
import com.education.service.SearchService;
import com.education.service.SegmentService;
import com.education.service.SynonymSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SynonymSearchService synonymSearchService;

    @Autowired
    private SearchHistoryMapper searchHistoryMapper;

    @Autowired
    private SegmentService  segmentService;


    /**
     * 搜索帖子，新增搜索历史
     * @param keyword
     * @return
     */
    @Override
    public Result search(String keyword) {
        List<PostVO> list = synonymSearchService.search(keyword, 10);

        // 存储搜索历史
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setUserId(BaseContext.getCurrentId());
        long snowflakeId = IdWorker.getId();
        searchHistory.setId(IdWorker.getId());
        searchHistory.setKeyword(keyword);
        searchHistory.setCreateTime(LocalDateTime.now());
        int rows = searchHistoryMapper.insert(searchHistory);
        if (rows == 0) {
            return Result.error(BusinessConstant.SEARCH_FAIL);
        }

        return Result.success(list);
    }

    /**
     * 查询用户搜索页面个性化信息
     * @return
     */
    @Override
    public Result querySearchPersonalInfo() {

        // 个人搜索历史
        List<SearchHistory> searchHistoryList = searchHistory();

        // 搜索发现
        List<String> searchFoundList = searchFound();

        SearchPersonalInfoVO searchPersonalInfoVO = new SearchPersonalInfoVO();
        searchPersonalInfoVO.setSearchHistoryList(searchHistoryList);
        searchPersonalInfoVO.setSearchFoundList(searchFoundList);

        return Result.success(searchPersonalInfoVO);
    }

    /**
     * 删除历史记录
     * @param id
     * @return
     */
    @Override
    public Result delSearchHistory(Long id) {
        if(id==null)
            return delAllSearchHistory();
        return delSingleSearchHistory(id);
    }

    private Result delSingleSearchHistory(Long id) {
        int row = searchHistoryMapper.deleteById(id);
        if (row == 0) {
            return Result.error(BusinessConstant.DELETE_FAIL);
        }
        return Result.success();
    }

    private Result delAllSearchHistory() {
        String userId = BaseContext.getCurrentId();
        int rows = searchHistoryMapper.delete(new QueryWrapper<SearchHistory>().eq("user_id", userId));
        if (rows == 0) {
            return Result.error(BusinessConstant.DELETE_FAIL);
        }
        return Result.success(rows);
    }

    private List<String> searchFound() {

        String userId = BaseContext.getCurrentId();
        List<String> keywords = searchHistoryMapper.selectList(
                        new QueryWrapper<SearchHistory>()
                                .eq("user_id", userId)
                                .orderByDesc("create_time")
                                .last("limit 8")
                                .select("keyword")
                )
                .stream()
                .map(SearchHistory::getKeyword) // 提取 keyword 字段值
                .collect(Collectors.toList());

        List<String> searchFoundList = new ArrayList<>();
        for (String keyword : keywords) {
            // 1. 分词处理（过滤停用词）
            List<String> words = segmentService.segmentWithStopWordFilter(keyword);

            // 2. 扩展同义词
            Set<String> expandedWords = synonymSearchService.expandKeywords(words);
            // 包含原始词和同义词
            expandedWords.addAll(words);
            searchFoundList.addAll(expandedWords);
        }

        return searchFoundList;
    }

    private List<SearchHistory> searchHistory() {
        String userId = BaseContext.getCurrentId();
        List<SearchHistory> searchHistoryList = searchHistoryMapper.selectList(
                new QueryWrapper<SearchHistory>().eq("user_id", userId)
        );
        return searchHistoryList;
    }

}
