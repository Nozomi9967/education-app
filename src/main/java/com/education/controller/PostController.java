package com.education.controller;

import com.education.constant.BusinessConstant;
import com.education.pojo.dto.InsertPostDTO;
import com.education.pojo.dto.PostUpdateDTO;
import com.education.pojo.vo.PostRawVO;
import com.education.result.PageResult;
import com.education.result.Result;
import com.education.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 帖子管理
 */
@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 新增帖子接口
     * @param insertPostDTO
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增帖子接口")
    public Result add(@Valid @RequestBody InsertPostDTO insertPostDTO) {
        return postService.insert(insertPostDTO);
    }

    /**
     * 删除帖子接口
     * @param postId
     * @return
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除帖子接口")
    public Result delete(@RequestParam Long postId) {
        return postService.remove(postId);
    }

    /**
     * 查询帖子接口
     * @param postId
     * @return
     */
    @GetMapping("/queryById")
    @Operation(summary = "查询帖子接口")
    public Result queryById(@RequestParam Long postId) {
        return postService.queryById(postId);
    }

    /**
     * 下拉加载帖子列表
     * @param cursor 上一页最后一条帖子的雪花ID（首次请求传null或0）
     * @param pageSize 每页条数（如20）
     * @return 帖子列表 + 下一页游标（最后一条的ID，为null表示无更多数据）
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询帖子接口")
    public Result<PageResult<PostRawVO>> page(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = BusinessConstant.DEFAULT_PAGE_SIZE) Integer pageSize) {
        return postService.page(cursor,pageSize);
    }


    /**
     * 修改帖子接口
     * @param postUpdateDTO
     * @return
     */
    @PutMapping("/modify")
    @Operation(summary = "修改帖子接口",description = "目前仅可以修改标题和内容")
    public Result modify(@Valid @RequestBody PostUpdateDTO postUpdateDTO) {
        return postService.update(postUpdateDTO);
    }


    /**
     * 点赞接口
     * @param postId
     * @return
     */
    @PostMapping("/like")
    @Operation(summary = "点赞接口")
    public Result like(@RequestParam Long postId) {
        return postService.like(postId);
    }

    /**
     * 取消点赞接口
     * @param postId
     * @return
     */
    @DeleteMapping("/unlike")
    @Operation(summary = "取消点赞接口")
    public Result unlike(@RequestParam Long postId) {
        return postService.unlike(postId);
    }
}
