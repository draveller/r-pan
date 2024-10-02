package com.imooc.pan.storage.engine.core;

import cn.hutool.core.lang.Assert;
import com.imooc.pan.cache.core.constants.CacheConstants;
import com.imooc.pan.storage.engine.core.context.DeleteFileContext;
import com.imooc.pan.storage.engine.core.context.MergeFileContext;
import com.imooc.pan.storage.engine.core.context.StoreFileChunkContext;
import com.imooc.pan.storage.engine.core.context.StoreFileContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.IOException;

/**
 * 顶级文件存储引擎的公用父类
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 公用的获取缓存的方法
     *
     * @return
     */
    protected Cache getCache() {
        if (this.cacheManager == null) {
            throw new RuntimeException("the cacheManager is empty");
        }
        return this.cacheManager.getCache(CacheConstants.R_PAN_CACHE_NAME);
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

    // ******************************** private ********************************

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
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notBlank(context.getIdentifier(), "文件唯一标识不能为空");
        Assert.notNull(context.getUserId(), "用户id不能为空");
        Assert.notEmpty(context.getRealPathList(), "文件分片路径列表不能为空");
    }

    /**
     * 校验保存文件分片的参数
     *
     * @param context
     */
    private void checkStoreFileChunkContext(StoreFileChunkContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notNull(context.getIdentifier(), "文件唯一标识不能为空");
        Assert.notNull(context.getTotalSize(), "文件大小不能为空");
        Assert.notNull(context.getInputStream(), "文件分片输入流不能为空");
        Assert.notNull(context.getTotalChunks(), "文件分片总数不能为空");
        Assert.notNull(context.getChunkNumber(), "文件分片序号不能为空");
        Assert.notNull(context.getCurrentChunkSize(), "文件分片的大小不能为空");
        Assert.notNull(context.getUserId(), "当前用户ID不能为空");
    }

    /**
     * 校验上传物理文件的上下文信息
     */
    private void checkDeleteFileContext(DeleteFileContext context) {
        Assert.notEmpty(context.getRealFilePathList(), "要删除的文件路径列表不能为空");
    }

    /**
     * 校验上传物理文件的上下文信息
     */
    private void checkStoreFileContext(StoreFileContext context) {

        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notNull(context.getTotalSize(), "文件大小不能为空");
        Assert.notNull(context.getInputStream(), "文件输入流不能为空");
    }

}
