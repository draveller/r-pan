package com.imooc.pan.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;

/**
 * 文件存储引擎存储物理文件的上下文实体
 */
@Data
public class StoreFileContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件大小
     */
    private Long totalSize;

    /**
     * 文件输入流
     */
    private transient InputStream inputStream;

    /**
     * 文件上传后的物理路径
     */
    private String realPath;

}
