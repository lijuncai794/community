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
     * @param userId 用户id;传入id等于0，表示：查询所有用户的帖子(首页全部帖子)
     *               传入id不等于0，表示：查询指定用户的帖子(个人页面中的帖子)
     * @param offset 起始行号
     * @param limit  每页最大显示数量
     * @return 帖子List
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 获取帖子数量
     * 其中@Param注解用于给参数取别名，如果只有一个参数并且在<if>中使用，则必须取别名
     *
     * @param userId 用户id
     * @return 帖子数量
     */
    int selectDiscussPostRows(@Param("userId") int userId);
}
