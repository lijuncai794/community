package com.lijuncai.learningbbs.service;

import com.lijuncai.learningbbs.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @description: 点赞相关的服务类
 * @author: lijuncai
 **/
@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞操作
     *
     * @param userId       点赞的用户id
     * @param entityType   实体类型
     * @param entityId     实体id
     * @param entityUserId 实体所属用户id
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        /*
        //获取这个实体在redis中用于存储点赞数据的key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        //在点赞前，判断用户是否已经对这个实体点过赞
        Boolean isAlreadyLike = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        //如果已经点过赞，则取消此点赞；如果没有点过赞，则进行点赞
        if (isAlreadyLike) {
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }
        */

        //在原有点赞的基础上，加入用户被点赞次数的统计，2个任务需要同时完成，因此使用事务来处理
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                boolean isAlreadyLike = operations.opsForSet().isMember(entityLikeKey, userId);

                //开启事务
                operations.multi();

                //若已经对此实体点过赞，则取消点赞，并将用户获得的赞的数量减1
                if (isAlreadyLike) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {//若没有对此实体点过赞，则执行点赞，并将用户获得的赞的数量加1
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                //提交事务
                return operations.exec();
            }
        });
    }

    /**
     * 查询某实体点赞的数量
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return 点赞的数量
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某用户对某实体的点赞状态
     * 使用int作为返回值,便于扩展其他功能，比如点踩
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return 点赞状态(0 : 未点赞, 1 : 已点赞)
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 查询用户获得点赞的数量
     *
     * @param userId 用户id
     * @return 获得点赞的数量
     */
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer likeCount = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return likeCount == null ? 0 : likeCount.intValue();
    }

}
