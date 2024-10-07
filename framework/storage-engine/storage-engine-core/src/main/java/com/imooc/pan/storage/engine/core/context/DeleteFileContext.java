package com.imooc.pan.storage.engine.core.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 删除物理文件的上下文信息
 */
@Data
public class DeleteFileContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 真实文件存储路径的集合
     */
    private List<String> realFilePathList;
}
