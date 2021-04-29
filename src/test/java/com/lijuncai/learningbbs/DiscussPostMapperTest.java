package com.lijuncai.learningbbs;

import com.lijuncai.learningbbs.dao.DiscussPostMapper;
import com.lijuncai.learningbbs.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @description: DiscussPostMapper的测试类
 * @author: lijuncai
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = LearningBbsApplication.class)
public class DiscussPostMapperTest {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectDiscussPosts() {
        List<DiscussPost> list= discussPostMapper.selectDiscussPosts(149,0,20);
        for (DiscussPost discussPost:list){
            System.out.println(discussPost);
        }
    }

    @Test
    public void testSelectDiscussPostRows() {
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println("rows:" + rows);
    }
}
