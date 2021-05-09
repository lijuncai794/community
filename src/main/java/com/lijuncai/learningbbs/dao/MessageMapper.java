package com.lijuncai.learningbbs.dao;

import com.lijuncai.learningbbs.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description: 消息相关的Mapper
 * @author: lijuncai
 **/
@Mapper
public interface MessageMapper {

    /**
     * 查询当前用户的会话列表,针对每个会话只返回一条最新的私信
     *
     * @param userId int 用户id
     * @param offset int 起始行号
     * @param limit  int 每页最大私信数量限制
     * @return List<Message> 私信列表
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量
     *
     * @param userId int 用户id
     * @return int 会话数量
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     *
     * @param conversationId String 会话id
     * @param offset         int 起始行号
     * @param limit          int 每页最大私信数量限制
     * @return List<Message> 私信列表
     */
    List<Message> selectLetters(String conversationId, int offset, int limit);

    /**
     * 查询某个会话所包含的私信数量
     *
     * @param conversationId String 会话id
     * @return int 私信数量
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读私信的数量
     *
     * @param userId         int 用户id
     * @param conversationId String 会话id
     * @return int 未读私信的数量
     */
    int selectLetterUnreadCount(int userId, String conversationId);

    /**
     * 新增私信
     *
     * @param message Message对象
     * @return int 受影响的行数
     */
    int insertMessage(Message message);

    /**
     * 修改私信的状态
     *
     * @param ids    List<Integer> 私信id列表
     * @param status int 新的私信状态
     * @return int 受影响的行数
     */
    int updateStatus(List<Integer> ids, int status);
}
