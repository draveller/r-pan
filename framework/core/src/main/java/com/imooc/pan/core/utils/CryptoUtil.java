package com.imooc.pan.core.utils;

import cn.hutool.crypto.symmetric.AES;

/**
 * 加解密工具类
 */
public class CryptoUtil {

    private CryptoUtil() {
    }

    private static final byte[] SECRET_KEY = "LUqjIh2rAAc/PL4x5H8wZWB6duyyL4NS".getBytes();

    private static final AES AES = new AES(SECRET_KEY);

    public static byte[] aesEncrypt(byte[] content) {
        return CryptoUtil.AES.encrypt(content);
    }

    public static byte[] aesDecode(byte[] content) {
        return CryptoUtil.AES.decrypt(content);
    }

}
