package com.education.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.pojo.entity.Course;
import com.education.pojo.vo.CourseJoinVO;
import com.education.pojo.vo.CourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 根据ID查询课程详情（包含创建者和兴趣圈信息）
     */
    @Select("SELECT c.*, u.nickname as creatorNickname, i.name as interestName " +
            "FROM edu_course c " +
            "LEFT JOIN edu_user u ON c.user_id = u.id " +
            "LEFT JOIN edu_interest i ON c.interest_id = i.id " +
            "WHERE c.id = #{id} AND c.delete_time IS NULL")
    CourseVO selectCourseVOById(Long id);

    /**
     * 查询指定兴趣圈的所有课程
     */
    @Select("SELECT c.*, u.nickname as creatorNickname, i.name as interestName " +
            "FROM edu_course c " +
            "LEFT JOIN edu_user u ON c.user_id = u.id " +
            "LEFT JOIN edu_interest i ON c.interest_id = i.id " +
            "WHERE c.interest_id = #{interestId} AND c.delete_time IS NULL")
    List<CourseVO> selectCourseVOByInterestId(Integer interestId);

    /**
     * 查询用户创建的所有课程
     */
    @Select("SELECT c.*, u.nickname as creatorNickname, i.name as interestName " +
            "FROM edu_course c " +
            "LEFT JOIN edu_user u ON c.user_id = u.id " +
            "LEFT JOIN edu_interest i ON c.interest_id = i.id " +
            "WHERE c.user_id = #{userId} AND c.delete_time IS NULL")
    List<CourseVO> selectCourseVOByUserId(String userId);

    CourseVO getCourseVOById(Long id);

    List<CourseJoinVO> myJoinCourse(String userId);
}
