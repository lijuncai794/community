package com.lijuncai.learningbbs.dao;

import com.lijuncai.learningbbs.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description: 评论相关的Mapper
 * @author: lijuncai
 **/
@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);
}
