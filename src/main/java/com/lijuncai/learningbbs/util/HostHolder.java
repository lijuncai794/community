package com.lijuncai.learningbbs.util;

import com.lijuncai.learningbbs.entity.User;
import org.springframework.stereotype.Component;

/**
 * @description: 持有用户信息, 用于代替session对象
 * ThreadLocal线程安全，每个线程有自己的map用于存放数据
 * @author: lijuncai
 **/
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    /**
     * 存入user对象
     *
     * @param user user对象
     */
    public void setUser(User user) {
        users.set(user);
    }

    /**
     * 获取user对象
     *
     * @return user对象
     */
    public User getUser() {
        return users.get();
    }

    /**
     * 清除user对象
     */
    public void clear() {
        users.remove();
    }
}
