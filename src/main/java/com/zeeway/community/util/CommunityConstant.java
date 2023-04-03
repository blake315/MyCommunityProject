package com.zeeway.community.util;

public interface CommunityConstant {

    /**
     * 0表示激活成功
     * 1表示重复激活
     * 2表示激活失败
     *
     * DEFAULT_EXPIRED_SECONDS:默认状态下的登录凭证超时时间
     * REMEMBER_EXPIRED_SECONDS:表示在勾选‘记住我’之后登录凭证的超时时间
     */

    int ACTIVATION_SUCCESS = 0;

    int ACTIVATION_REPEAT = 1;

    int ACTIVATION_FAILURE = 2;

    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;


    /**
     * 实体类型常量
     * 1 == 帖子
     * 2 == 评论
     * 3 == 用户
     */

    int ENTITY_TYPE_POST = 1;

    int ENTITY_TYPE_COMMENT = 2;

    int ENTITY_TYPE_USER = 3;
}
