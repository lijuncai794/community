package com.lijuncai.learningbbs;

import com.lijuncai.learningbbs.dao.UserMapper;
import com.lijuncai.learningbbs.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @description: UserMapper的测试类
 * @author: lijuncai
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = LearningBbsApplication.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(12);
        System.out.println(user);

        user = userMapper.selectByName("zhangfei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder128@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        //id不用设置，Mybatis会填入数据自动生成的值

        user.setUsername("lijuncai");
        user.setPassword("lijuncai794");
        user.setSalt("jnhu");
        user.setEmail("lijuncai@gmail.com");
        user.setHeaderUrl("http://www.google.com/001.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "002.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "cquptlijuncai");
        System.out.println(rows);
    }
}
