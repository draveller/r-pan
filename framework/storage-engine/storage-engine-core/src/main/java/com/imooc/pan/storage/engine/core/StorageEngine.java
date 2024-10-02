package com.imooc.pan.storage.engine.core;

import com.imooc.pan.storage.engine.core.context.DeleteFileContext;
import com.imooc.pan.storage.engine.core.context.StoreFileChunkContext;
import com.imooc.pan.storage.engine.core.context.StoreFileContext;

import java.io.IOException;

/**
 * 文件存储引擎的顶级接口
 */
public interface StorageEngine {

    /**
     * 存储物理文件
     *
     * @param context
     * @throws IOException
     */
    void store(StoreFileContext context) throws IOException;

    /**
     * 删除物理文件
     *
     * @param context
     * @throws IOException
     */
    void delete(DeleteFileContext context) throws IOException;

    /**
     * 存储物理文件的分片
     *
     * @param context
     * @throws IOException
     */
    void storeChunk(StoreFileChunkContext context) throws IOException;

}
