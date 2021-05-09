package com.lijuncai.learningbbs.service;

import com.lijuncai.learningbbs.dao.DiscussPostMapper;
import com.lijuncai.learningbbs.entity.DiscussPost;
import com.lijuncai.learningbbs.util.LearningBbsConstant;
import com.lijuncai.learningbbs.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @description: 帖子相关的服务类
 * @author: lijuncai
 **/
@Service
public class DiscussPostService implements LearningBbsConstant {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * @param userId int 用户id;传入id等于0，表示：查询所有用户的帖子(首页全部帖子)
     *               传入id不等于0，表示：查询指定用户的帖子(个人页面中的帖子)
     * @param offset int 起始行号
     * @param limit  int 每页最大显示数量
     * @return List<DiscussPost> 帖子列表
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

    /**
     * 添加帖子
     *
     * @param post DiscussPost对象
     * @return int 受影响的行数
     */
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("新增帖子失败，参数为空!");
        }

        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    /**
     * 通过帖子id获取帖子
     *
     * @param id int 帖子id
     * @return DiscussPost 帖子对象
     */
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    /**
     * 更新帖子的评论数量
     *
     * @param id           int 帖子id
     * @param commentCount int 新的评论数量
     * @return int 受影响的行数
     */
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }


    /**
     * 删除一条帖子(将帖子的status修改为2)
     *
     * @param id int 帖子id
     * @return int 受影响的行数
     */
    public int deleteDiscussPostById(int id) {
        return discussPostMapper.updatePostStatus(id, POST_STATUS_DELETE);
    }

    /**
     * 根据关键词搜索帖子
     *
     * @param keyword String 关键词
     * @param offset  int 起始行号
     * @param limit   int 每页最大显示数量
     * @return List<DiscussPost> 帖子列表
     */
    public List<DiscussPost> search(String keyword, int offset, int limit) {
        return discussPostMapper.search(keyword, offset, limit);
    }

    /**
     * 查询此关键词可搜索出的帖子数量，用于设置分页
     *
     * @param keyword String 关键词
     * @return int 关键词可搜索出的帖子数量
     */
    public int selectSearchTotal(String keyword) {
        return discussPostMapper.selectSearchTotal(keyword);
    }
}
