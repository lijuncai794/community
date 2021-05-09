package com.lijuncai.learningbbs.dao;

import com.lijuncai.learningbbs.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description: 用户相关的Mapper
 * @author: lijuncai
 **/

//@Repository 用这两个注解都可以，一个是Spring提供的，一个是Mybatis提供的
@Mapper
public interface UserMapper {

    /**
     * 根据id查询用户
     *
     * @param id int 用户id
     * @return User对象
     */
    User selectById(int id);

    /**
     * 根据用户名查询用户
     *
     * @param username String 用户名
     * @return User对象
     */
    User selectByName(String username);

    /**
     * 根据用户邮箱查询用户
     *
     * @param email String 用户邮箱
     * @return User对象
     */
    User selectByEmail(String email);

    /**
     * 新增用户
     *
     * @param user User对象
     * @return int 受影响的行数
     */
    int insertUser(User user);

    /**
     * 修改用户状态
     *
     * @param id     int 用户id
     * @param status int 新的用户状态
     * @return int 受影响的行数
     */
    int updateStatus(int id, int status);

    /**
     * 修改用户头像
     *
     * @param id        int 用户id
     * @param headerUrl String 新的头像地址
     * @return int 受影响的行数
     */
    int updateHeader(int id, String headerUrl);

    /**
     * 修改用户密码
     *
     * @param id       int 用户id
     * @param password String 新的用户密码
     * @return int 受影响的行数
     */
    int updatePassword(int id, String password);
}
