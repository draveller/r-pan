package com.imooc.pan.storage.engine.core;

import com.imooc.pan.cache.core.constants.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

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


}
