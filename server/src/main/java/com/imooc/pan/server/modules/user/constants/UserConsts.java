package com.imooc.pan.server.modules.user.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 用户模块的常量类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserConsts {


    /**
     * 登录用户的用户id的key值
     */
    public static final String LOGIN_USER_ID = "LOGIN_USER_ID";

    /**
     * 用户登录缓存前缀
     */
    public static final String USER_LOGIN_PREFIX = "USER_LOGIN_";

    /**
     * 24小时的毫秒值
     */
    public static final Long ONE_DAY_LONG = 24L * 3600L * 1000L;

    /**
     * 5分钟的毫秒值
     */
    public static final Long FIVE_MINUTES_LONG = 300L * 1000L;

    /**
     * 用户忘记密码-重置密码 临时token的key
     */
    public static final String FORGET_USERNAME = "FORGET_USERNAME";

    /**
     * 用户名校验正则表达式
     */
    public static final String USERNAME_REGEXP = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,16}$";

}
