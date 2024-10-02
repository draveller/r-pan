package com.imooc.pan.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 保存文件分片的上下文信息
 */
@Data
public class StoreFileChunkContext implements Serializable {

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件标识
     */
    private String identifier;

    /**
     * 文件大小
     */
    private Long totalSize;

    /**
     * 文件输入流
     */
    private InputStream inputStream;

    /**
     * 文件的真实存储路径
     */
    private String realPath;

    /**
     * 文件总分片数
     */
    private Integer totalChunks;

    /**
     * 当前分片的序号
     */
    private Integer chunkNumber;

    /**
     * 当前分片的大小
     */
    private Long currentChunkSize;

    /**
     * 用户ID
     */
    private Long userId;
}
