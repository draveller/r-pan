package com.imooc.pan.server.modules.file.context;

import lombok.Data;

import java.util.List;

/**
 * 查询文件列表上下文实体
 */
@Data
public class QueryFileListContext {

    /**
     * 父文件夹id
     */
    private Long parentId;

    /**
     * 文件类型集合
     */
    private List<Integer> fileTypeArray;

    /**
     * 当前的登录用户
     */
    private Long userId;

    /**
     * 文件删除标识
     */
    private Integer delFlag;

}
