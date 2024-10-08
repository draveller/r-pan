package com.imooc.pan.core.utils;

import com.imooc.pan.core.constants.GlobalConst;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件相关的工具类
 */
public class FileUtil {

    private FileUtil() {
    }

    /**
     * 根据文件名称获取后缀
     */
    public static String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename) || !filename.contains(GlobalConst.POINT_STR)) {
            return StringUtils.EMPTY;
        }

        return filename.substring(filename.lastIndexOf(GlobalConst.POINT_STR));
    }

}
