package com.lijuncai.learningbbs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @description: Redis测试类
 * @author: lijuncai
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = LearningBbsApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString() {
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey, 1, 60, TimeUnit.SECONDS);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHash() {
        String redisKey = "test:person";
        redisTemplate.opsForHash().put(redisKey, "name", "zhangsan");
        redisTemplate.opsForHash().put(redisKey, "age", "20");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "name"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "age"));
    }

    @Test
    public void testList() {
        String redisKey = "test:ids";

        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSet() {
        String redisKey = "test:stu";
        redisTemplate.opsForSet().add(redisKey, "stu1", "stu2", "stu3");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));//返回集合的所有元素
    }

    @Test
    public void testSortedSet() {
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 90);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 50);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 70);
        redisTemplate.opsForZSet().add(redisKey, "白龙马", 60);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));//元素的数量
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒"));//元素的分数
        //rank()默认由小到大排序,reverseRank()由大到小排序，返回的是索引
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒"));
        //range()默认由小到大取区间的元素进行排序,reverseRange则在区间由大到小排序，返回的是元素key
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
    }

    @Test
    public void testKeys() {
        redisTemplate.delete("test:students");

        System.out.println(redisTemplate.hasKey("test:students"));

        redisTemplate.expire("test:stu", 20, TimeUnit.SECONDS);
    }

    /**
     * 多次访问同一个key,不想每次都指定key去操作,可以采用绑定操作
     */
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
//        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps("testhash");
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    /**
     * Redis支持事务,实现方式是：开启事务后，里面的操作都存入队列，等最后一起提交
     * 在事务里面的查询操作不会生效
     */
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:uname";

                redisOperations.multi();//开启事务

                redisOperations.opsForSet().add(redisKey, "zhangsan");
                redisOperations.opsForSet().add(redisKey, "lisi");
                redisOperations.opsForSet().add(redisKey, "wangwu");

                System.out.println(redisOperations.opsForSet().members(redisKey));//测试在事务中查询，结果返回的是：[]

                return redisOperations.exec();//提交事务
            }
        });
        System.out.println(obj);//输出结果：[1, 1, 1, [lisi, zhangsan, wangwu]]，其中的1代表受影响的行数
    }
}
