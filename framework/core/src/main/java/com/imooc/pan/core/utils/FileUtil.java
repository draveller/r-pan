package com.imooc.pan.core.utils;

import cn.hutool.core.util.StrUtil;
import com.imooc.pan.core.constants.GlobalConst;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 文件相关的工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {

    /**
     * 根据文件名称获取后缀
     */
    public static String getFileSuffix(String filename) {
        if (StrUtil.isBlank(filename) || !filename.contains(GlobalConst.POINT_STR)) {
            return StrUtil.EMPTY;
        }
        return filename.substring(filename.lastIndexOf(GlobalConst.POINT_STR));
    }

}
