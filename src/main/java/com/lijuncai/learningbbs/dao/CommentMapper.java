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
    /**
     * 通过实体类型查询其相应的评论
     *
     * @param entityType int 实体类型
     * @param entityId   int 实体id
     * @param offset     int 起始行号
     * @param limit      int 每页最大数量限制
     * @return List<Comment> 评论列表
     */
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 通过实体查询其相应的评论数量
     *
     * @param entityType int 实体类型
     * @param entityId   int 实体id
     * @return int 评论数量
     */
    int selectCountByEntity(int entityType, int entityId);

    /**
     * 插入一条评论
     *
     * @param comment Comment对象
     * @return int 受影响的行
     */
    int insertComment(Comment comment);

    /**
     * 修改评论的状态
     *
     * @param id     int 评论id
     * @param status int 评论状态
     * @return int 受影响的行
     */
    int updateCommentStatus(int id, int status);

    /**
     * 通过id查找所属用户的id
     *
     * @param id int 评论id
     * @return int 所属用户的id
     */
    int selectUserIdById(int id);

    /**
     * 通过id查找所属实体的id
     *
     * @param id int 评论id
     * @return int 所属实体的id
     */
    int selectEntityIdById(int id);

    /**
     * 通过id查找所属实体的类型
     *
     * @param id int 评论id
     * @return int 所属实体的id
     */
    int selectEntityTypeById(int id);
}
