package com.imooc.pan.storage.engine.core;

import cn.hutool.core.lang.Assert;
import com.imooc.pan.cache.core.constants.CacheConst;
import com.imooc.pan.core.constants.MsgConst;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.storage.engine.core.context.*;
import jakarta.annotation.Resource;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.IOException;

/**
 * 顶级文件存储引擎的公用父类
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    @Resource
    private CacheManager cacheManager;

    /**
     * 公用的获取缓存的方法
     */
    protected Cache getCache() {
        if (this.cacheManager == null) {
            throw new RPanBusinessException("the cacheManager is empty");
        }
        return this.cacheManager.getCache(CacheConst.R_PAN_CACHE_NAME);
    }

    /**
     * 存储物理文件
     * 1. 参数校验
     * 2. 执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void store(StoreFileContext context) throws IOException {
        this.checkStoreFileContext(context);
        this.doStore(context);
    }

    /**
     * 执行保存物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context
     */
    protected abstract void doStore(StoreFileContext context) throws IOException;

    /**
     * 删除物理文件
     * 1. 参数校验
     * 2. 执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void delete(DeleteFileContext context) throws IOException {
        this.checkDeleteFileContext(context);
        this.doDelete(context);
    }

    /**
     * 执行删除物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context
     */
    protected abstract void doDelete(DeleteFileContext context) throws IOException;

    /**
     * 存储物理文件的分片
     * 1. 参数校验
     * 2. 执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void storeChunk(StoreFileChunkContext context) throws IOException {
        this.checkStoreFileChunkContext(context);
        this.doStoreChunk(context);
    }

    /**
     * 执行保存文件分片
     * 下沉到底层去实现
     *
     * @param context
     * @throws IOException
     */
    protected abstract void doStoreChunk(StoreFileChunkContext context) throws IOException;

    /**
     * 合并文件分片
     * 1. 检查参数
     * 2. 执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void mergeFile(MergeFileContext context) throws IOException {
        this.checkMergeFileContext(context);
        this.doMergeFile(context);
    }

    /**
     * 1. 参数校验
     * 2. 执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void readFile(ReadFileContext context) throws IOException {
        this.checkReadFileContext(context);
        this.doReadFile(context);
    }

    /**
     * 读取文件内容并写入到输出流中
     * 下沉到子类去实现
     */
    protected abstract void doReadFile(ReadFileContext context) throws IOException;

    // ******************************** private ********************************

    /**
     * 文件读取参数校验
     *
     * @param context
     */
    private void checkReadFileContext(ReadFileContext context) {
        Assert.notBlank(context.getRealPath(), MsgConst.FILE_REAL_PATH_CANNOT_BE_EMPTY);
        Assert.notNull(context.getOutputStream(), MsgConst.FILE_OUTPUT_STREAM_CANNOT_BE_EMPTY);
    }

    /**
     * 执行文件分片的动作
     * 下沉到子类实现
     *
     * @param context
     */
    protected abstract void doMergeFile(MergeFileContext context) throws IOException;

    /**
     * 检查合并文件分片的参数
     *
     * @param context
     */
    private void checkMergeFileContext(MergeFileContext context) {
        Assert.notBlank(context.getFilename(), MsgConst.FILE_NAME_CANNOT_BE_EMPTY);
        Assert.notBlank(context.getIdentifier(), MsgConst.FILE_UNIFICATION_CANNOT_BE_EMPTY);
        Assert.notNull(context.getUserId(), MsgConst.USER_ID_CANNOT_BE_EMPTY);
        Assert.notEmpty(context.getRealPathList(), MsgConst.FILE_CHUNK_PATHS_CANNOT_BE_EMPTY);
    }

    /**
     * 校验保存文件分片的参数
     *
     * @param context
     */
    private void checkStoreFileChunkContext(StoreFileChunkContext context) {
        Assert.notBlank(context.getFilename(), MsgConst.FILE_NAME_CANNOT_BE_EMPTY);
        Assert.notNull(context.getIdentifier(), MsgConst.FILE_UNIFICATION_CANNOT_BE_EMPTY);
        Assert.notNull(context.getTotalSize(), MsgConst.FILE_SIZE_CANNOT_BE_EMPTY);
        Assert.notNull(context.getInputStream(), MsgConst.FILE_INPUT_STREAM_CANNOT_BE_EMPTY);
        Assert.notNull(context.getTotalChunks(), MsgConst.FILE_TOTAL_CHUNKS_CANNOT_BE_EMPTY);
        Assert.notNull(context.getChunkNumber(), MsgConst.FILE_CHUNK_NUMBER_CANNOT_BE_EMPTY);
        Assert.notNull(context.getCurrentChunkSize(), MsgConst.FILE_CHUNK_SIZE_CANNOT_BE_EMPTY);
        Assert.notNull(context.getUserId(), MsgConst.USER_ID_CANNOT_BE_EMPTY);
    }

    /**
     * 校验上传物理文件的上下文信息
     */
    private void checkDeleteFileContext(DeleteFileContext context) {
        Assert.notEmpty(context.getRealFilePathList(), MsgConst.FILE_PATHS_CANNOT_BE_EMPTY);
    }

    /**
     * 校验上传物理文件的上下文信息
     */
    private void checkStoreFileContext(StoreFileContext context) {
        Assert.notBlank(context.getFilename(), MsgConst.FILE_NAME_CANNOT_BE_EMPTY);
        Assert.notNull(context.getTotalSize(), MsgConst.FILE_SIZE_CANNOT_BE_EMPTY);
        Assert.notNull(context.getInputStream(), MsgConst.FILE_INPUT_STREAM_CANNOT_BE_EMPTY);
    }

}
