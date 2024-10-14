package com.imooc.pan.core.utils;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.CharsetUtil;
import com.imooc.pan.core.exception.RPanBusinessException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 雪花算法id生成器
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityIdUtil {

    /**
     * 加密Long类型为字符串
     *
     * @param value Long类型的值
     * @return 加密后的字符串
     */
    public static String encrypt(Long value) {
        if (value == null) {
            throw new RPanBusinessException("需要加密的Long类型值为空");
        }
        return CryptoUtil.encryptString(String.valueOf(value));
    }

    /**
     * 解密字符串为Long类型
     *
     * @param encryptedStr 加密后的字符串
     * @return 解密后的Long类型值
     */
    public static Long decrypt(String encryptedStr) {
        if (encryptedStr == null) {
            throw new RPanBusinessException("需要解码的字符串为空");
        }

        if (CryptoUtil.isUrlEncoded(encryptedStr)) {
            encryptedStr = URLDecoder.decode(encryptedStr, CharsetUtil.CHARSET_UTF_8);
        }
        return Long.valueOf(CryptoUtil.decryptString(encryptedStr));
    }

}
