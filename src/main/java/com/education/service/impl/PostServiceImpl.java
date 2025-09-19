package com.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.education.constant.BusinessConstant;
import com.education.context.BaseContext;
import com.education.exception.BusinessException;
import com.education.mapper.InterestMapper;
import com.education.mapper.PostMapper;
import com.education.mapper.UserLikeMapper;
import com.education.mapper.UserMapper;
import com.education.pojo.dto.InsertPostDTO;
import com.education.pojo.dto.PostUpdateDTO;
import com.education.pojo.entity.Interest;
import com.education.pojo.entity.Post;
import com.education.pojo.entity.User;
import com.education.pojo.entity.UserLike;
import com.education.pojo.vo.PostRawVO;
import com.education.pojo.vo.PostVO;
import com.education.result.PageResult;
import com.education.result.Result;
import com.education.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private UserLikeMapper  userLikeMapper;

    @Autowired
    private InterestMapper interestMapper;

    @Override
    public Result insert(InsertPostDTO insertPostDTO) {
        // 1.检验用户是否存在（
        boolean userExist = userMapper.exists(
                new QueryWrapper<User>().eq("id", insertPostDTO.getUserId())
                        .isNull("delete_time") // 排除已注销用户
        );
        if (!userExist) {
            return Result.error(BusinessConstant.USER_NOT_EXIST);
        }

        // 2.检验兴趣圈是否存在且启用（应查询兴趣圈表）
        boolean interestExist = interestMapper.exists(
                new QueryWrapper<Interest>().eq("id", insertPostDTO.getInterestId())
                        .eq("status", 1) // 1-启用状态
                        .isNull("delete_time")
        );
        if (!interestExist) {
            return Result.error(BusinessConstant.INTEREST_NOT_EXIST);
        }

        // 3.转换为实体类并设置属性
        Post post = new Post();
        BeanUtils.copyProperties(insertPostDTO, post);

        // 4.使用雪花算法生成ID（MP自带的IdWorker）
        long snowflakeId = IdWorker.getId();
        post.setId(snowflakeId); // 转换为字符串存储（匹配表结构char(64)）

        // 5.设置默认值和时间
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setForwardCount(0);
        post.setStatus(1); // 默认为已发布状态（1-已发布）
        LocalDateTime now = LocalDateTime.now();
        post.setCreateTime(now);
        post.setUpdateTime(now);
        post.setDeleteTime(null); // 初始无删除时间

        // 6.执行插入
        int rows = postMapper.insert(post);
        if (rows > 0) {
            log.info("帖子发布成功，ID：{}，用户ID：{}", post.getId(), insertPostDTO.getUserId());
            return Result.success(post.getId());
        } else {
            log.error("帖子发布失败，参数：{}", insertPostDTO);
            return Result.error(BusinessConstant.INSERT_FAIL);
        }
    }

    /**
     * 删除帖子
     * @param postId
     * @return
     */
    @Override
    public Result remove(Long postId) {
        // 1.检验参数
        String currentUserId = BaseContext.getCurrentId();
        if(currentUserId == null || postId == null) {
            return Result.error(BusinessConstant.POST_DELETE_PARAM_IS_NULL);
        }

        // 2.查询帖子是否存在且属于当前用户
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return Result.error(BusinessConstant.POST_NOT_EXIST);
        }

        // 3.验证帖子所属用户是否匹配
        if (!post.getUserId().equals(currentUserId)) {
            return Result.error(BusinessConstant.DELETE_NO_PERMISSION);
        }

        // 4.验证帖子是否已被删除
        if (post.getDeleteTime() != null) {
            return Result.error(BusinessConstant.POST_ALREADY_DELETE);
        }

        // 5.执行逻辑删除（更新delete_time和update_time）
        Post updatePost = new Post();
        updatePost.setId(postId);
        updatePost.setStatus(BusinessConstant.INACTIVE_STATUS);
        LocalDateTime now = LocalDateTime.now();
        updatePost.setDeleteTime(now);
        updatePost.setUpdateTime(now);

        int rows = postMapper.updateById(updatePost);
        if (rows > 0) {
            log.info("帖子逻辑删除成功，postId:{}，userId:{}", postId, currentUserId);
            return Result.success("帖子删除成功");
        } else {
            log.error("帖子删除失败，postId:{}，userId:{}", postId, currentUserId);
            return Result.error(BusinessConstant.DELETE_FAIL);
        }
    }

    /**
     * 根据postId查询
     *
     * @param postId
     * @return
     */
    @Override
    public Result queryById(Long postId) {
        // 1.检验参数
        if(postId == null || BaseContext.getCurrentId() == null) {
            return Result.error(BusinessConstant.PARAM_IS_NULL);
        }

        // 2.关联查询帖子、用户和兴趣圈信息
        Map<String,Object> map = new HashMap<>();
        map.put("postId",postId);
        map.put("userId",BaseContext.getCurrentId());
        PostVO postVO = postMapper.queryPostDetail(map);

        if (postVO == null) {
            return Result.error(BusinessConstant.POST_NOT_EXIST);
        }

        return Result.success(postVO);
    }

    /**
     * 分页查询帖子
     *
     * @param cursor    游标（上一页最后一条帖子的ID）
     * @param pageSize  每页条数
     * @return 分页结果
     */
    @Override
    public Result<PageResult<PostRawVO>> page(String cursor, Integer pageSize) {
        // 1. 限制pageSize最大值，防止恶意请求
        pageSize = Math.min(pageSize, BusinessConstant.MAX_PAGE_SIZE);

        // 2. 获取当前登录用户ID（实际项目中从上下文获取）
        String userId = BaseContext.getCurrentId();

        // 3. 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("cursor", cursor);
        params.put("pageSize", pageSize);
        params.put("userId", userId);

        // 4. 调用mapper查询当前页数据
        List<PostRawVO> postList = postMapper.pagePosts(params);

        // 5. 计算下一页游标
        Long nextCursor = null;
        if (postList.size() == pageSize) {
            // 如果当前页数据量等于pageSize，说明可能还有下一页
            nextCursor = postList.get(postList.size() - 1).getId();
        }

        // 6. 封装分页结果
        PageResult<PostRawVO> pageResult = new PageResult<>();
        pageResult.setRecords(postList);
        pageResult.setCursor(nextCursor);

        return Result.success(pageResult);
    }

    /**
     * 修改帖子
     * @param postUpdateDTO
     * @return
     */
    @Override
    public Result update(PostUpdateDTO postUpdateDTO) {
        // 1.检验当前用户是否为帖子的发布者
        String userId = BaseContext.getCurrentId();
        boolean isExist = postMapper.exists(
                new QueryWrapper<Post>().eq("id", postUpdateDTO.getId())
                        .isNull("delete_time")
                        .eq("user_id", userId)
        );
        if (!isExist) {
            return Result.error(BusinessConstant.UPDATE_FAIL);
        }

        // 2.转换类型
        Post post = new Post();
        BeanUtils.copyProperties(postUpdateDTO, post);
        post.setUpdateTime(LocalDateTime.now());

        // 3.更新数据库
        int rows = postMapper.updateById(post);
        if (rows > 0) {
            return Result.success(post.getId());
        }
        return Result.error(BusinessConstant.UPDATE_FAIL);

    }


    /**
     * 新增点赞关系
     * @param postId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result like(Long postId) {
        if (postId == null) {
            return Result.error(BusinessConstant.PARAM_IS_NULL);
        }

        String userId = BaseContext.getCurrentId();

        boolean isExist = userLikeMapper.exists(
                new QueryWrapper<UserLike>().eq("user_id", userId)
                        .eq("post_id", postId)
        );
        if(isExist) {
            return Result.error("该用户已点赞过");
        }



        int rows = postMapper.incrementLikeCount(postId);
        if (rows == 0){
            throw new BusinessException("点赞数更新失败");
        }


        UserLike userLike = new UserLike();
        userLike.setUserId(userId);
        userLike.setPostId(postId);
        userLike.setCreateTime(LocalDateTime.now());
        int rows2 = userLikeMapper.insert(userLike);


        if (rows2 == 0) {
            throw new BusinessException("点赞数更新失败");
        }
        return Result.success();
    }

    /**
     * 删除点赞关系
     * @param postId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result unlike(Long postId) {
        if (postId == null) {
            return Result.error(BusinessConstant.PARAM_IS_NULL);
        }

        int rows1 = postMapper.decrementLikeCount(postId);
        if (rows1 == 0)
            throw new  BusinessException("取消点赞失败");

        String userId = BaseContext.getCurrentId();
        int rows = userLikeMapper.delete(new QueryWrapper<UserLike>().eq("user_id", userId).eq("post_id", postId));
        if (rows == 0)
            throw new  BusinessException("取消点赞失败");
        return Result.success();
    }
}
