package com.lijuncai.learningbbs.service;

import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.util.LearningBbsConstant;
import com.lijuncai.learningbbs.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description: 关注相关的服务类
 * @author: lijuncai
 **/
@Service
public class FollowService implements LearningBbsConstant {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 关注
     * 使用事务来实现，因为有两个操作：1被关注者的粉丝增加、2关注数量增加
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @param entityId   实体id
     */
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //获取到redis中要操作的key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                //开启事务
                redisOperations.multi();
                //将数据存入有序集合
                redisOperations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                //提交事务执行
                return redisOperations.exec();
            }
        });
    }

    /**
     * 取消关注，主要逻辑同关注
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @param entityId   实体id
     */
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    /**
     * 查询用户关注的实体的数量
     * entityType=2代表关注了多少帖子,entityType=3代表关注了多少用户
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @return long 用户关注的实体的数量
     */
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 查询实体的粉丝数量
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return long 实体的粉丝数量
     */
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 查询当前用户是否已关注该实体
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return boolean 是否已关注该实体
     */
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    /**
     * 查询某用户关注的人
     *
     * @param userId 用户id
     * @param offset 查询偏移
     * @param limit  每页最大显示数量
     * @return List<Map < String, Object>>关注列表
     */
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        //获取到关注的人的id集合，根据关注时间的倒序返回
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (targetIds == null) {
            return null;
        }

        //遍历id，查询到相应用户
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            //将用户加入map
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            //将关注时间加入map
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    /**
     * 查询某用户的粉丝
     *
     * @param userId 用户id
     * @param offset 查询偏移
     * @param limit  每页最大显示数量
     * @return List<Map < String, Object>>粉丝列表
     */
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }

        return list;
    }
}
