package com.lijuncai.learningbbs.dao;

import com.lijuncai.learningbbs.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @description: 登录凭证相关的Mapper
 * @author: lijuncai
 **/
@Mapper
@Deprecated //登录凭证使用redis方案来存储了,此注解用于声明此类“不推荐使用”
public interface LoginTicketMapper {
    //除了可以在配置文件写sql，还可以利用注解来写sql

    /**
     * 新增一个登录凭证
     *
     * @param loginTicket 登录凭证
     * @return
     */
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 通过凭证来查询此登录凭证所有的信息
     *
     * @param ticket
     * @return
     */
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     * 更新登录凭证的信息
     * 使用<script>标签也可以在这里使用动态sql语句
     *
     * @param ticket
     * @param status
     * @return
     */
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);
}
