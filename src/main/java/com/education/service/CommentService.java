package com.education.service;

import com.education.pojo.dto.CommentUpdateDTO;
import com.education.pojo.dto.InsertCommentDTO;
import com.education.pojo.vo.CommentVO;
import com.education.result.PageResult;
import com.education.result.Result;

import javax.validation.Valid;

public interface CommentService {


    /**
     * 新增评论
     * @param insertCommentDTO
     * @return
     */
    Result insert(InsertCommentDTO insertCommentDTO);

    /**
     * 删除评论
     * @param commentId
     * @return
     */
    Result remove(Long commentId);

    /**
     * 分页查询评论
     *
     * @param postId
     * @param cursor
     * @param pageSize
     * @return
     */
    Result<PageResult<CommentVO>> page(Long postId, Long cursor, Integer pageSize);

    /**
     * 修改评论内容
     * @param commentUpdateDTO
     * @return
     */
    Result modify(CommentUpdateDTO commentUpdateDTO);
}
