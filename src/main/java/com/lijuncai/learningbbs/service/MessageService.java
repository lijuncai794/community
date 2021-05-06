package com.lijuncai.learningbbs.service;

import com.lijuncai.learningbbs.dao.MessageMapper;
import com.lijuncai.learningbbs.entity.Message;
import com.lijuncai.learningbbs.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @description: 消息相关的服务类
 * @author: lijuncai
 **/
@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 查询用户的会话，每个会话只显示最新的一条消息
     *
     * @param userId 用户id
     * @param offset 偏移
     * @param limit  每页最多可显示的会话数量
     * @return 会话列表
     */
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    /**
     * 查询用户的会话数量
     *
     * @param userId 用户id
     * @return 会话数量
     */
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * 查询会话的消息
     *
     * @param conversationId 会话id
     * @param offset         偏移量
     * @param limit          每个会话页面最多可显示的消息数量
     * @return
     */
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    /**
     * 查询会话的消息数量
     *
     * @param conversationId 会话id
     * @return 消息数量
     */
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    /**
     * 查询用户的未读消息数量
     *
     * @param userId         用户id
     * @param conversationId 会话id
     * @return 未读消息数量
     */
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    /**
     * 新增消息
     * 对消息进行标签转义、敏感词过滤之后再插入数据库
     *
     * @param message 消息对象
     * @return 受影响的行数
     */
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    /**
     * 阅读消息，将消息的状态改为已读
     *
     * @param ids 消息列表
     * @return 受影响的行数
     */
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

}
