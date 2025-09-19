package com.education.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.pojo.entity.Comment;
import com.education.pojo.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    public List<CommentVO> pageComments(Map<String, Object> map);
}
