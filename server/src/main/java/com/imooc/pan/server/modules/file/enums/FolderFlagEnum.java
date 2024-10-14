package com.imooc.pan.server.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件夹标识枚举类
 */
@AllArgsConstructor
@Getter
public enum FolderFlagEnum {

    /**
     * 不是文件夹
     */
    NO(0),

    /**
     * 是文件夹
     */
    YES(1);

    private final Integer code;

    public static FolderFlagEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FolderFlagEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
