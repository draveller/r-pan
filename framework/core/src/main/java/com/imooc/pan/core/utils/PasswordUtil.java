package com.imooc.pan.core.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 密码工具类
 * Created by RubinChu on 2021/1/22 下午 4:11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordUtil {

    /**
     * 随机生成盐值
     */
    public static String getSalt() {
        return RandomUtil.randomString(16);
    }

    /**
     * 密码加密
     */
    public static String encryptPassword(String salt, String inputPwd) {
        return SecureUtil.sha256(SecureUtil.sha1(inputPwd) + salt);
    }

}
