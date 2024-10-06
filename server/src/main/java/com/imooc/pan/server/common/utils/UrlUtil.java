package com.imooc.pan.server.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 前端传的参数字符串, 有时候需要解码, 有时候则不需要...
 * 为了适配前端传参, 所以创建此工具类..
 */
public class UrlUtil {

    public static boolean needsUrlDecode(String input) {
        // 检查字符串中是否包含百分号（%）
        if (input.contains("%")) {
            // 尝试解码
            try {
                String decoded = URLDecoder.decode(input, "UTF-8");
                // 比较解码前后的字符串
                return !input.equals(decoded);
            } catch (UnsupportedEncodingException e) {
                // 如果解码失败，说明字符串可能不需要解码
                return false;
            }
        }
        // 如果没有百分号，说明不需要解码
        return false;
    }

    public static String decodeIfNeeds(String input) {
        try {
            return needsUrlDecode(input) ? URLDecoder.decode(input, "UTF-8") : input;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}