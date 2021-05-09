package com.lijuncai.learningbbs.service;

import com.lijuncai.learningbbs.dao.CommentMapper;
import com.lijuncai.learningbbs.entity.Comment;
import com.lijuncai.learningbbs.util.LearningBbsConstant;
import com.lijuncai.learningbbs.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @description: 评论相关的服务类
 * @author: lijuncai
 **/
@Service
public class CommentService implements LearningBbsConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 查找实体的评论
     *
     * @param entityType int 实体类型
     * @param entityId   int 实体id
     * @param offset     int 起始行号
     * @param limit      int 最大数量限制
     * @return List<Comment> 评论列表
     */
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 获取实体的评论数量
     *
     * @param entityType int 实体类型
     * @param entityId   int 实体id
     * @return int 评论数量
     */
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * 新增评论
     * 事务处理：新增评论、更新帖子表中的评论数量
     *
     * @param comment Comment对象
     * @return int 受影响的行数
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("添加评论失败，参数为空!");
        }

        //先对内容进行标签处理、过滤处理再添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        //更新帖子的评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    /**
     * 删除评论,将评论的状态修改为2
     * 事务处理：删除评论、更新帖子表中的评论数量
     *
     * @param id int 评论id
     * @return int 受影响的行数
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int deleteCommentById(int id) {
        int rows = commentMapper.updateCommentStatus(id, COMMENT_STATUS_DELETE);
        int entityId = commentMapper.selectEntityIdById(id);
        int entityType = commentMapper.selectEntityTypeById(id);

        //更新帖子的评论数量
        if (entityType == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(entityType, entityId);
            discussPostService.updateCommentCount(entityId, count);
        }
        return rows;
    }

    /**
     * 通过id查找所属用户的id
     *
     * @param id int 评论id
     * @return int 所属用户的id
     */
    public int selectUserIdById(int id) {
        return commentMapper.selectUserIdById(id);
    }

}
