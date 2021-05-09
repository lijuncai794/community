package com.lijuncai.learningbbs.dao;

import com.lijuncai.learningbbs.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 帖子相关的Mapper
 * @author: lijuncai
 **/
@Mapper
public interface DiscussPostMapper {

    /**
     * @param userId int 用户id;传入id等于0，表示：查询所有用户的帖子(首页全部帖子)
     *               传入id不等于0，表示：查询指定用户的帖子(个人页面中的帖子)
     * @param offset int 起始行号
     * @param limit  int 每页最大数量限制
     * @return List<DiscussPost> 帖子列表
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 获取帖子数量
     * 其中@Param注解用于给参数取别名，如果只有一个参数并且在<if>中使用，则必须取别名
     *
     * @param userId int 用户id
     * @return int 帖子数量
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 新增帖子
     *
     * @param discussPost DiscussPost对象
     * @return int 受影响的行数
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 通过帖子id获取帖子对象
     *
     * @param id int 帖子id
     * @return DiscussPost 帖子对象
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 更新帖子的评论数量
     *
     * @param id           int 帖子id
     * @param commentCount int 新的评论数量
     * @return int 受影响的行数
     */
    int updateCommentCount(int id, int commentCount);

    /**
     * 修改帖子的状态
     *
     * @param id     int 帖子id
     * @param status int 新的帖子状态
     * @return int 受影响的行数
     */
    int updatePostStatus(int id, int status);

    /**
     * 根据关键词搜索帖子
     *
     * @param keyword String 关键词
     * @param offset  int 起始行号
     * @param limit   int 每页最大显示数量
     * @return List<DiscussPost> 帖子列表
     */
    List<DiscussPost> search(String keyword, int offset, int limit);

    /**
     * 查询此关键词可搜索出的帖子数量，用于设置分页
     *
     * @param keyword String 关键词
     * @return int 此关键词可搜索出的帖子数量
     */
    int selectSearchTotal(String keyword);
}
