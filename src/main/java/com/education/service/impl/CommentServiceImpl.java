package com.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.education.constant.BusinessConstant;
import com.education.context.BaseContext;
import com.education.mapper.CommentMapper;
import com.education.mapper.PostMapper;
import com.education.mapper.UserMapper;
import com.education.pojo.dto.CommentUpdateDTO;
import com.education.pojo.dto.InsertCommentDTO;
import com.education.pojo.entity.Comment;
import com.education.pojo.entity.Post;
import com.education.pojo.entity.User;
import com.education.pojo.vo.CommentVO;
import com.education.result.PageResult;
import com.education.result.Result;
import com.education.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper; // 校验用户合法性

    @Autowired
    private PostMapper postMapper; // 校验帖子合法性


    /**
     * 新增评论：校验用户/帖子存在性 + 封装默认值 + 插入数据库
     */
    @Override
    public Result insert(InsertCommentDTO insertCommentDTO) {
        // 1. 校验登录状态（从上下文获取当前用户ID，与PostService逻辑一致）
        String currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            log.error("新增评论失败：用户未登录");
            return Result.error(BusinessConstant.LOGIN_EXPIRED);
        }

        // 2. 校验用户是否存在（排除已注销用户）
        boolean userExist = userMapper.exists(
                new QueryWrapper<User>()
                        .eq("id", currentUserId)
                        .isNull("delete_time")
        );
        if (!userExist) {
            log.error("新增评论失败：用户不存在，userId={}", currentUserId);
            return Result.error(BusinessConstant.USER_NOT_EXIST);
        }

        // 3. 校验帖子是否存在（排除已删除/未发布的帖子）
        Long postId = insertCommentDTO.getPostId();
        Post post = postMapper.selectById(postId);
        if (post == null || post.getDeleteTime() != null || post.getStatus() != 1) { // 1=已发布状态
            log.error("新增评论失败：帖子不存在或已下架，postId={}", postId);
            return Result.error(BusinessConstant.POST_NOT_EXIST);
        }

        // 4. 转换DTO为实体，设置默认值（雪花算法ID、初始点赞数等）
        Comment comment = new Comment();
        BeanUtils.copyProperties(insertCommentDTO, comment);
        comment.setUserId(currentUserId); // 绑定当前登录用户
        comment.setId(IdWorker.getId()); // MP雪花算法生成唯一ID
        comment.setLikeCount(0); // 初始点赞数为0
        LocalDateTime now = LocalDateTime.now();
        comment.setCreateTime(now);
        comment.setUpdateTime(now);
        comment.setDeleteTime(null); // 初始未删除

        // 5. 执行插入（复用MP的BaseMapper方法）
        int rows = commentMapper.insert(comment);
        if (rows > 0) {
            log.info("新增评论成功：commentId={}, postId={}, userId={}",
                    comment.getId(), postId, currentUserId);
            // 可选：更新帖子的评论数（与PostService的统计逻辑对齐）
            post.setCommentCount(post.getCommentCount() + 1);
            postMapper.updateById(post);
            return Result.success(comment.getId()); // 返回新增评论ID
        } else {
            log.error("新增评论失败：数据库插入异常，参数={}", insertCommentDTO);
            return Result.error(BusinessConstant.INSERT_FAIL);
        }
    }


    /**
     * 删除评论：校验权限（仅本人可删） + 逻辑删除（更新delete_time）
     */
    @Override
    public Result remove(Long commentId) {
        // 1. 校验参数和登录状态
        String currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null || commentId == null) {
            log.error("删除评论失败：参数不全，userId={}, commentId={}", currentUserId, commentId);
            return Result.error(BusinessConstant.PARAM_IS_NULL);
        }

        // 2. 校验评论是否存在（排除已删除的评论）
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleteTime() != null) {
            log.error("删除评论失败：评论不存在或已删除，commentId={}", commentId);
            return Result.error(BusinessConstant.COMMENT_NOT_EXIST);
        }

        // 3. 校验权限（仅评论作者可删，与PostService的权限逻辑一致）
        if (!currentUserId.equals(comment.getUserId())) {
            log.error("删除评论失败：无权限，currentUserId={}, commentOwnerId={}",
                    currentUserId, comment.getUserId());
            return Result.error(BusinessConstant.DELETE_NO_PERMISSION);
        }

        // 4. 执行逻辑删除（更新delete_time和update_time，不物理删除）
        Comment updateComment = new Comment();
        updateComment.setId(commentId);
        LocalDateTime now = LocalDateTime.now();
        updateComment.setDeleteTime(now);
        updateComment.setUpdateTime(now);

        int rows = commentMapper.updateById(updateComment);
        if (rows > 0) {
            log.info("删除评论成功：commentId={}, userId={}", commentId, currentUserId);
            // 可选：更新帖子的评论数（递减）
            Post post = postMapper.selectById(comment.getPostId());
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postMapper.updateById(post);
            return Result.success("评论删除成功");
        } else {
            log.error("删除评论失败：数据库更新异常，commentId={}", commentId);
            return Result.error(BusinessConstant.DELETE_FAIL);
        }
    }


    /**
     * 分页查询评论：对接XML的pageComments方法（游标分页+关注状态判断）
     */
    @Override
    public Result<PageResult<CommentVO>> page(Long postId, Long cursor, Integer pageSize) {
        // 1. 校验基础参数（与PostService的分页参数校验逻辑一致）
        String currentUserId = BaseContext.getCurrentId();
        if (postId == null || pageSize == null || currentUserId == null) {
            log.error("评论分页查询失败：参数不全，postId={}, cursor={}, pageSize={}",
                    postId, cursor, pageSize);
            return Result.error(BusinessConstant.PARAM_IS_NULL);
        }

        // 2. 限制pageSize最大值（防止恶意请求，参考PostService的MAX_PAGE_SIZE）
        pageSize = Math.min(pageSize, BusinessConstant.MAX_PAGE_SIZE);

        // 3. 校验帖子是否存在（排除已删除/未发布的帖子）
        Post post = postMapper.selectById(postId);
        if (post == null || post.getDeleteTime() != null || post.getStatus() != 1) {
            log.error("评论分页查询失败：帖子不存在或已下架，postId={}", postId);
            return Result.error(BusinessConstant.POST_NOT_EXIST);
        }

        // 4. 构建XML查询参数（与XML中的#{params.xxx}对应）
        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId);       // 帖子ID（筛选该帖子的评论）
        params.put("cursor", cursor);       // 游标（上一页最后一条评论的ID，用于倒序分页）
        params.put("pageSize", pageSize);   // 每页条数
        params.put("currentUserId", currentUserId); // 当前登录用户ID（判断是否关注作者）

        // 5. 调用XML中的pageComments方法查询数据（自定义SQL逻辑）
        List<CommentVO> commentVOList = commentMapper.pageComments(params);

        // 6. 计算下一页游标（与PostService的游标逻辑一致：数据满页则下一页游标为最后一条ID）
        Long nextCursor = null;
        if (!CollectionUtils.isEmpty(commentVOList) && commentVOList.size() == pageSize) {
            nextCursor = commentVOList.get(commentVOList.size() - 1).getId();
        }

        // 7. 封装分页结果（与PostService的PageResult结构一致）
        PageResult<CommentVO> pageResult = new PageResult<>();
        pageResult.setRecords(commentVOList); // 当前页评论列表
        pageResult.setCursor(nextCursor);     // 下一页游标（null表示无下一页）

        log.info("评论分页查询成功：postId={}, currentUserId={}, 数据量={}, 下一页游标={}",
                postId, currentUserId, commentVOList.size(), nextCursor);
        return Result.success(pageResult);
    }


    /**
     * 修改评论内容：校验权限 + 更新数据库
     */
    @Override
    public Result modify(CommentUpdateDTO commentUpdateDTO) {
        // 1. 校验参数和登录状态
        String currentUserId = BaseContext.getCurrentId();
        if (commentUpdateDTO == null || commentUpdateDTO.getId() == null
                || currentUserId == null) {
            log.error("修改评论失败：参数不全，commentUpdateDTO={}, userId={}",
                    commentUpdateDTO, currentUserId);
            return Result.error(BusinessConstant.PARAM_IS_NULL);
        }

        // 2. 校验评论是否存在（排除已删除的评论）
        Long commentId = commentUpdateDTO.getId();
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleteTime() != null) {
            log.error("修改评论失败：评论不存在或已删除，commentId={}", commentId);
            return Result.error(BusinessConstant.COMMENT_NOT_EXIST);
        }

        // 3. 校验权限（仅评论作者可修改，与PostService的修改权限逻辑一致）
        if (!currentUserId.equals(comment.getUserId())) {
            log.error("修改评论失败：无权限，currentUserId={}, commentOwnerId={}",
                    currentUserId, comment.getUserId());
            return Result.error(BusinessConstant.UPDATE_NO_PERMISSION);
        }

        // 4. 转换DTO为实体，更新内容和时间
        Comment updateComment = new Comment();
        updateComment.setId(commentId);
        updateComment.setContent(commentUpdateDTO.getContent()); // 新评论内容
        updateComment.setUpdateTime(LocalDateTime.now());        // 更新时间

        // 5. 执行更新（复用MP的BaseMapper方法）
        int rows = commentMapper.updateById(updateComment);
        if (rows > 0) {
            log.info("修改评论成功：commentId={}, userId={}", commentId, currentUserId);
            return Result.success("评论修改成功");
        } else {
            log.error("修改评论失败：数据库更新异常，commentId={}", commentId);
            return Result.error(BusinessConstant.UPDATE_FAIL);
        }
    }
}