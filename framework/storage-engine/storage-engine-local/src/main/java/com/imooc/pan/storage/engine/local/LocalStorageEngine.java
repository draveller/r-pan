package com.imooc.pan.storage.engine.local;

import com.imooc.pan.core.utils.FileUtils;
import com.imooc.pan.storage.engine.core.AbstractStorageEngine;
import com.imooc.pan.storage.engine.core.context.DeleteFileContext;
import com.imooc.pan.storage.engine.core.context.StoreFileChunkContext;
import com.imooc.pan.storage.engine.core.context.StoreFileContext;
import com.imooc.pan.storage.engine.local.config.LocalStorageEngineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * 本地文件存储引擎实现类
 */
@Component
public class LocalStorageEngine extends AbstractStorageEngine {

    @Autowired
    private LocalStorageEngineConfig config;

    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String basePath = this.config.getRootFilePath();
        String realFilePath = FileUtils.generateStoreFileRealPath(basePath, context.getFilename());
        FileUtils.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
        context.setRealPath(realFilePath);
    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        FileUtils.deleteFiles(context.getRealFilePathList());
    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        String basePath = this.config.getRootFileChunkPath();
        String realFilePath = FileUtils.generateStoreFileChunkRealPath(
                basePath, context.getIdentifier(), context.getChunkNumber());
        FileUtils.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
        context.setRealPath(realFilePath);
    }

}
