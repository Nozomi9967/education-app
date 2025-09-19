package com.education.service;

import com.education.pojo.dto.InsertPostDTO;
import com.education.pojo.dto.PostUpdateDTO;
import com.education.pojo.vo.PostRawVO;
import com.education.result.PageResult;
import com.education.result.Result;

import javax.validation.Valid;

public interface PostService {
    /**
     * 新增帖子
     * @param insertPostDTO
     * @return
     */
    Result insert(InsertPostDTO insertPostDTO);

    /**
     * 删除帖子
     * @param postId
     * @return
     */
    Result remove(Long postId);

    /**
     * 根据postId查询
     *
     * @param postId
     * @return
     */
    Result queryById(Long postId);

    /**
     * 分页查询帖子
     *
     * @param cursor
     * @param pageSize
     * @return
     */
    Result<PageResult<PostRawVO>> page(String cursor, Integer pageSize);

    /**
     * 修改帖子
     * @param postUpdateDTO
     * @return
     */
    Result update(PostUpdateDTO postUpdateDTO);


    /**
     * 新增点赞关系
     * @param postId
     * @return
     */
    Result like(Long postId);

    /**
     * 删除点赞关系
     * @param postId
     * @return
     */
    Result unlike(Long postId);
}
