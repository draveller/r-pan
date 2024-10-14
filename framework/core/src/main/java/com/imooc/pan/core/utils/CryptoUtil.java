package com.imooc.pan.core.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * 加解密工具类
 */
public class CryptoUtil {

    private static final SymmetricCrypto aes = new SymmetricCrypto(
            SymmetricAlgorithm.AES, "LUqjIh2rAAc/PL4x5H8wZWB6duyyL4NS".getBytes());

    private CryptoUtil() {
    }

    public static String encryptString(String string) {
        return aes.encryptBase64(string);
    }

    public static String decryptString(String encryptedStr) {
        return aes.decryptStr(encryptedStr, CharsetUtil.CHARSET_UTF_8);
    }

}
