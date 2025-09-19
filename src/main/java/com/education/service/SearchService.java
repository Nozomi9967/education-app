package com.education.service;


import com.education.result.Result;

public interface SearchService {

    /**
     * 搜索帖子，新增搜索历史
     * @param keyword
     * @return
     */
    Result search(String keyword);


    /**
     * 查询用户搜索页面个性化信息
     * @return
     */
    Result querySearchPersonalInfo();

    /**
     * 删除历史记录
     * @param id
     * @return
     */
    Result delSearchHistory(Long id);
}
