package com.imooc.pan.server.modules.file.context;

import com.imooc.pan.server.modules.file.entity.RPanFile;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件分片合并参数上下文
 */
@Data
public class FileChunkMergeContext implements Serializable {
    private static final long serialVersionUID = 1L;

    private String filename;

    private String identifier;

    private Long totalSize;

    private Long parentId;

    private Long userId;

    private RPanFile record;
}
