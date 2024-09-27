package com.imooc.pan.core.constants;


import org.apache.commons.lang3.StringUtils;

/**
 * RPan公用基础常量类
 */
public interface RPanConstants {

    /**
     * 公用的字符串分隔符
     */
    String COMMON_SEPARATOR = "__,__";

    /**
     * 空字符串
     */
    String EMPTY_STR = StringUtils.EMPTY;

    /**
     * 点常量
     */
    String POINT_STR = ".";

    /**
     * 斜线
     */
    String SLASH_STR = "/";

    /**
     * Long类型的0
     */
    Long ZERO_LONG = 0L;

    /**
     * Integer类型的0
     */
    Integer ZERO_INT = 0;

    /**
     * Integer类型的1
     */
    Integer ONE_INT = 1;

    /**
     * Integer类型的2
     */
    Integer TWO_INT = 2;

    /**
     * Integer类型的-1
     */
    Integer MINUS_ONE_INT = -1;

    /**
     * TRUE字符串
     */
    String TRUE_STR = "true";

    /**
     * FALSE字符串
     */
    String FALSE_STR = "false";

    /**
     * 组件扫描基础路径
     */
    String BASE_COMPONENT_SCAN_PATH = "com.imooc.pan";

}
