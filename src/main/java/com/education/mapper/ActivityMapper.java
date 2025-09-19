package com.education.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.pojo.dto.ActivityDTO;
import com.education.pojo.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * ActivityMapper
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    /**
     * 游标分页查询活动列表（包含兴趣圈信息）
     */
    List<ActivityDTO> pageActivities(Map<String, Object> params);

    /**
     * 根据ID查询活动详情（包含兴趣圈信息）
     */
    @Select("SELECT a.*, i.name as interest_name FROM edu_activity a " +
            "LEFT JOIN edu_interest i ON a.interest_id = i.id " +
            "WHERE a.id = #{id} AND a.delete_time IS NULL")
    ActivityDTO selectActivityById(@Param("id") Long id);

    /**
     * 根据用户ID查询用户创建的活动
     */
    @Select("SELECT a.*, i.name as interest_name FROM edu_activity a " +
            "LEFT JOIN edu_interest i ON a.interest_id = i.id " +
            "WHERE a.user_id = #{userId} AND a.delete_time IS NULL " +
            "ORDER BY a.create_time DESC")
    List<ActivityDTO> selectActivityByUserId(@Param("userId") String userId);

    /**
     * 根据兴趣圈ID查询活动
     */
    @Select("SELECT a.*, i.name as interest_name FROM edu_activity a " +
            "LEFT JOIN edu_interest i ON a.interest_id = i.id " +
            "WHERE a.interest_id = #{interestId} AND a.delete_time IS NULL AND a.status = 1 " +
            "ORDER BY a.create_time DESC")
    List<ActivityDTO> selectActivityByInterestId(@Param("interestId") Integer interestId);
}