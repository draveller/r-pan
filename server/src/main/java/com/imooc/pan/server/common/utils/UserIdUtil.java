package com.imooc.pan.server.common.utils;

import com.imooc.pan.core.constants.RPanConstants;

/**
 * 用户id存储工具类
 */
public class UserIdUtil {

    private UserIdUtil() {
    }

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户id
     *
     * @param userId
     */
    public static void set(Long userId) {
        UserIdUtil.threadLocal.set(userId);
    }

    /**
     * 获取当前线程的用户id
     *
     * @return
     */
    public static Long get() {
        Long userId = UserIdUtil.threadLocal.get();
        if (userId == null) {
            return RPanConstants.ZERO_LONG;
        }
        return userId;
    }

}
