package com.imooc.pan.server.modules.user.constants;

/**
 * 用户模块的常量类
 */
public interface UserConstants {

    /**
     * 登录用户的用户id的key值
     */
    String LOGIN_USER_ID = "LOGIN_USER_ID";

    /**
     * 用户登录缓存前缀
     */
    String USER_LOGIN_PREFIX = "USER_LOGIN_";

    /**
     * 24小时的毫秒值
     */
    Long ONE_DAY_LONG = 24L * 3600L * 1000L;

}
