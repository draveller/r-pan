package com.imooc.pan.core.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 加解密工具类
 */
public class CryptoUtil {

    private CryptoUtil() {
    }

    private static final Pattern URL_ENCODED_PATTERN = Pattern.compile("%[0-9A-Fa-f]{2}");

    private static final SymmetricCrypto aes = new SymmetricCrypto(
            SymmetricAlgorithm.AES, "LUqjIh2rAAc/PL4x5H8wZWB6duyyL4NS".getBytes());

    public static boolean isUrlEncoded(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        Matcher matcher = URL_ENCODED_PATTERN.matcher(str);
        return matcher.find();
    }

    public static String encryptString(String string) {
        return aes.encryptBase64(string);
    }

    public static String decryptString(String encryptedStr) {
        return aes.decryptStr(encryptedStr, CharsetUtil.CHARSET_UTF_8);
    }

}
