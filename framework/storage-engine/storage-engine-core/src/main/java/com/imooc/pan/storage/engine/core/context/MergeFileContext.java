package com.imooc.pan.storage.engine.core.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 合并文件上下文对象
 */
@Data
public class MergeFileContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 当前用户ID
     */
    private Long userId;

    /**
     * 文件分片的真实存储路径集合
     */
    private List<String> realPathList;

    /**
     * 文件合并后的真实存储路径
     */
    private String realPath;
}
