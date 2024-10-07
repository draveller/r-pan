package com.imooc.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件秒传上下文实体对象
 */
@Data
public class SecUploadFileContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件夹id
     */
    private Long parentId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 用户id
     */
    private Long userId;
}
