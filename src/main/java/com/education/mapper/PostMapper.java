package com.education.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.pojo.entity.Post;
import com.education.pojo.vo.PostRawVO;
import com.education.pojo.vo.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 根据postId查询帖子详细
     * @param postId
     * @return
     */
    public PostVO queryPostDetail(Map<String, Object> postId);

    /**
     * 分页查询帖子
     * @param params
     * @return
     */
    List<PostRawVO> pagePosts(Map<String, Object> params);

    @Update("UPDATE edu_post SET like_count = like_count + 1 , update_time = NOW() WHERE id = #{postId} and delete_time is null and status = 1")
    int incrementLikeCount(Long postId);

    @Update("UPDATE edu_post SET like_count = like_count - 1 , update_time = NOW() WHERE id = #{postId} and delete_time is null and status = 1")
    int decrementLikeCount(Long postId);
}
