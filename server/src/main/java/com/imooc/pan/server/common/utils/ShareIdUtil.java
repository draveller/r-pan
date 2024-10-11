package com.imooc.pan.server.common.utils;

import com.imooc.pan.core.constants.GlobalConst;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 分享ID存储工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShareIdUtil {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的分享ID
     */
    public static void set(Long shareId) {
        threadLocal.set(shareId);
    }

    /**
     * 获取当前线程的分享ID
     */
    public static Long get() {
        Long shareId = threadLocal.get();
        if (Objects.isNull(shareId)) {
            return GlobalConst.ZERO_LONG;
        }
        return shareId;
    }

}
