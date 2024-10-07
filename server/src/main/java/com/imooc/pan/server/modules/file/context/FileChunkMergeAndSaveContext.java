package com.imooc.pan.server.modules.file.context;

import com.imooc.pan.server.modules.file.entity.RPanFile;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件分片合并参数上下文
 */
@Data
public class FileChunkMergeAndSaveContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String filename;

    private String identifier;

    private Long totalSize;

    private Long parentId;

    private Long userId;

    /**
     * 物理文件记录
     */
    private RPanFile entity;

    /**
     * 文件合并后存储的真实路径
     */
    private String realPath;
}
