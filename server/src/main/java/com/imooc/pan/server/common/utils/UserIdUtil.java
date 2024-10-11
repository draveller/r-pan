package com.imooc.pan.server.common.utils;

import com.imooc.pan.core.constants.GlobalConst;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 用户id存储工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserIdUtil {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户id
     */
    public static void set(Long userId) {
        UserIdUtil.threadLocal.set(userId);
    }

    /**
     * 获取当前线程的用户id
     */
    public static Long get() {
        Long userId = UserIdUtil.threadLocal.get();
        if (userId == null) {
            return GlobalConst.ZERO_LONG;
        }
        return userId;
    }

}
