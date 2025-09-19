package com.education.controller;

import com.education.constant.BusinessConstant;
import com.education.pojo.dto.CommentUpdateDTO;
import com.education.pojo.dto.InsertCommentDTO;
import com.education.pojo.vo.CommentVO;
import com.education.result.PageResult;
import com.education.result.Result;
import com.education.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 新增评论接口
     * @param insertCommentDTO
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增评论接口")
    public Result add(@Valid @RequestBody InsertCommentDTO insertCommentDTO) {
        return commentService.insert(insertCommentDTO);
    }

    /**
     * 删除评论接口
     * @param commentId
     * @return
     */
    @DeleteMapping("/delete/{commentId}")
    @Operation(summary = "删除评论接口")
    public Result delete(@PathVariable Long commentId) {
        return commentService.remove(commentId);
    }

    /**
     * 根据postId分页查询评论
     * @param postId
     * @param cursor
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "根据postId分页查询评论接口")
    public Result<PageResult<CommentVO>> page(
            @RequestParam Long postId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = BusinessConstant.DEFAULT_PAGE_SIZE) Integer pageSize
    ) {
        return commentService.page(postId,cursor,pageSize);
    }

    /**
     * 修改评论内容
     * @param commentUpdateDTO
     * @return
     */
    @PutMapping("/modify")
    @Operation(summary = "修改评论内容")
    public Result modify(@Valid @RequestBody CommentUpdateDTO commentUpdateDTO) {
        return commentService.modify(commentUpdateDTO);
    }
}
