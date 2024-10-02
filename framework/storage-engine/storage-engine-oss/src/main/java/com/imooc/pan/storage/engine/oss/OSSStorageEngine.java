package com.imooc.pan.storage.engine.oss;

import com.imooc.pan.storage.engine.core.AbstractStorageEngine;
import com.imooc.pan.storage.engine.core.context.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 基于OSS的文件存储引擎实现
 */
@Component
public class OSSStorageEngine extends AbstractStorageEngine {

    @Override
    protected void doStore(StoreFileContext context) throws IOException {

    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {

    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {

    }

    @Override
    protected void doMergeFile(MergeFileContext context)  throws IOException{

    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException{

    }
}
