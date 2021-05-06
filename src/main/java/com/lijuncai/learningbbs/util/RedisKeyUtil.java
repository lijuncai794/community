package com.lijuncai.learningbbs.util;

/**
 * @description: 定义redis中各类key的工具类
 * @author: lijuncai
 **/
public class RedisKeyUtil {

    private static final String SPLIT = ":";//一个单词由多个拼成时，用:分隔符
    private static final String PREFIX_ENTITY_LIKE = "like:entity";//实体的"赞"的前缀
    private static final String PREFIX_USER_LIKE = "like:user";//用户获得的"赞"的前缀
    private static final String PREFIX_FOLLOWEE = "followee";//用户关注了多少实体(用户、帖子)
    private static final String PREFIX_FOLLOWER = "follower";//实体拥有多少粉丝(用户有粉丝、帖子有收藏)


    /**
     * 获取某个实体的赞在redis中的key
     * key格式为： like:entity:entityType:entityId
     * value内容为: 使用set来存储userId,不仅可以查看点赞数量，还可以实现查询是谁点的赞
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return 实体在redis中"赞"的key
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取某个用户的赞在redis中的key
     * key格式为： like:user:userId
     * value内容为: 获得赞的数量
     *
     * @param userId 用户id
     * @return 用户在redis中"赞"的key
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }


    // followee:userId:entityType -> zset(entityId,now)

    /**
     * 某个用户关注的实体
     * key格式：followee:userId:entityType
     * value内容：zset(entityId,nowTime),根据当前时间的整数形式排序
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @return 用户关注的实体在redis中的key
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝
     * key格式：follower:entityType:entityId
     * value内容：zset(userId,nowTime),根据当前时间的整数形式排序
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return 实体拥有的粉丝在redis中的key
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }
}
