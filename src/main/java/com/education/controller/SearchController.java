package com.education.controller;

import com.education.result.Result;
import com.education.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索接口管理
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;


    /**
     * 搜索帖子，新增搜索历史
     * @param keyword
     * @return
     */
    @GetMapping("/post")
    @Operation(summary = "搜索帖子")
    public Result search(@RequestParam String keyword) {
        return searchService.search(keyword);
    }

    /**
     * 查询用户搜索页面个性化信息
     * @return
     */
    @GetMapping("/search-history")
    @Operation(summary = "查询用户搜索页面个性化信息")
    public Result querySearchPersonalInfo() {
        return searchService.querySearchPersonalInfo();
    }


    /**
     * 删除历史记录接口
     * @param id
     * @return
     */
    @DeleteMapping("/del-search-history")
    @Operation(summary = "删除历史记录接口",description = "如果不加参数则全部清除")
    public Result delSearchHistory(@RequestParam(required = false) Long id) {
        return searchService.delSearchHistory(id);
    }
}
