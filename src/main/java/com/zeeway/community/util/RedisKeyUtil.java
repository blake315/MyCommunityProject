package com.zeeway.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";


    //某个实体的赞储存形式如下：
    //like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
    //某个用户获得的赞   like:user:userId  -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId ;
    }
    //用户的关注   followee:userId:entityType  -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //用户的粉丝|被关注  follower:entityType:entityId  -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

}
