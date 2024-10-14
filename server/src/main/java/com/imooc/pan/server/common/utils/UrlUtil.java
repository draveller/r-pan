package com.imooc.pan.server.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 前端传的参数字符串, 有时候需要解码, 有时候则不需要...
 * 为了适配前端传参, 所以创建此工具类..
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlUtil {

    public static boolean needsUrlDecode(String input) {
        // 检查字符串中是否包含百分号（%）
        if (input.contains("%")) {
            // 尝试解码
            String decoded = URLDecoder.decode(input, StandardCharsets.UTF_8);
            // 比较解码前后的字符串
            return !input.equals(decoded);
        }
        // 如果没有百分号，说明不需要解码
        return false;
    }

    public static String decodeIfNeeds(String input) {
        return needsUrlDecode(input) ? URLDecoder.decode(input, StandardCharsets.UTF_8) : input;
    }

}