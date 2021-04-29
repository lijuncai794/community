package com.lijuncai.learningbbs.service;

import com.lijuncai.learningbbs.dao.DiscussPostMapper;
import com.lijuncai.learningbbs.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: DiscussPost的服务类
 * @author: lijuncai
 **/
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     * @param userId 用户id;传入id等于0，表示：查询所有用户的帖子(首页全部帖子)
     *               传入id不等于0，表示：查询指定用户的帖子(个人页面中的帖子)
     * @param offset 起始行号
     * @param limit  每页最大显示数量
     * @return 帖子List
     */
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    /**
     * 获取帖子数量
     *
     * @param userId 用户id
     * @return 帖子数量
     */
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
