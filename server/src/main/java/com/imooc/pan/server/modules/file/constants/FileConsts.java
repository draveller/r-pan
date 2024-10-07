package com.imooc.pan.server.modules.file.constants;

/**
 * 文件模块常量类
 */
public final class FileConsts {

    private FileConsts() {
    }

    /**
     * 顶级父文件id
     */
    public static final Long TOP_PARENT_ID = 0L;

    /**
     * 根文件夹名称
     */
    public static final String ALL_FILE_CN_STR = "全部文件";

    /**
     * 中文左括号
     */
    public static final String CN_LEFT_PARENTHESIS_STR = "（";

    /**
     * 中文右括号
     */
    public static final String CN_RIGHT_PARENTHESIS_STR = "）";

    /**
     * 表示所有文件类型
     */
    public static final String ALL_FILE_TYPE = "-1";

    public static final String CONTENT_TYPE_STR = "Content-Type";

    /**
     * 文件内容的部署方式
     */
    public static final String CONTENT_DISPOSITION_STR = "Content-Disposition";

    /**
     * 以附件的方式下载
     */
    public static final String CONTENT_DISPOSITION_VALUE_PREFIX_STR = "attachment;fileName=";

    public static final String GB2312_STR = "GB2312";

    public static final String ISO_8859_1_STR = "ISO-8859-1";

}
