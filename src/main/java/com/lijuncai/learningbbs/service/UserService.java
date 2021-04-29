package com.lijuncai.learningbbs.service;

import com.lijuncai.learningbbs.dao.UserMapper;
import com.lijuncai.learningbbs.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: User的服务类
 * @author: lijuncai
 **/
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户id查询
     *
     * @param id 用户id
     * @return User对象
     */
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

}
