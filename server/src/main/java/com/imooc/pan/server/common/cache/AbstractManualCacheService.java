package com.imooc.pan.server.common.cache;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.pan.cache.core.constants.CacheConst;
import com.imooc.pan.core.exception.RPanBusinessException;
import jakarta.annotation.Resource;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 手动处理缓存的公用顶级弗雷
 *
 * @param <V>
 */
public abstract class AbstractManualCacheService<V> implements ManualCacheService<V> {

    private final Object lock = new Object();
    /**
     * 注入属性时设置 required = false, 防止在某些情况下报错
     */
    @Resource
    @Nullable
    private CacheManager cacheManager;

    protected abstract BaseMapper<V> getBaseMapper();

    @Override
    public List<V> getByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return ids.stream().map(this::getById).toList();
    }

    @Override
    public boolean updateByIds(Map<? extends Serializable, V> entityMap) {
        if (MapUtil.isEmpty(entityMap)) {
            return false;
        }
        Set<? extends Map.Entry<? extends Serializable, V>> entries = entityMap.entrySet();
        for (Map.Entry<? extends Serializable, V> entry : entries) {
            if (this.updateById(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeBatchByIds(Collection<?> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        for (Object id : ids) {
            if (!this.removeById((Serializable) id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Cache getCache() {
        if (this.cacheManager == null) {
            throw new RPanBusinessException("the cache manager is empty");
        }
        return this.cacheManager.getCache(CacheConst.R_PAN_CACHE_NAME);
    }

    /**
     * 1. 查询缓存, 如果命中就直接返回
     * 2. 如果没有命中就查询数据库
     * 3. 如果数据库有对应的记录, 回填缓存
     */
    @Override
    public V getById(Serializable id) {
        V result = this.getByCache(id);
        if (result != null) {
            return result;
        }
        // 使用锁机制来避免缓存击穿
        synchronized (this.lock) {
            result = this.getByCache(id);
            if (result != null) {
                return result;
            }

            result = this.getByDB(id);
            if (result != null) {
                // 回填缓存
                this.putCache(id, result);
            }

        }
        return result;
    }

    /**
     * 根据id来更新缓存信息
     */
    @Override
    public boolean updateById(Serializable id, V entity) {
        BaseMapper<V> baseMapper = this.getBaseMapper();
        int affectRows = baseMapper.updateById(entity);
        this.removeCache(id);
        return affectRows > 0;
    }

    @Override
    public boolean removeById(Serializable id) {
        int affectRows = this.getBaseMapper().deleteById(id);
        this.removeCache(id);
        return affectRows > 0;
    }

    // -------------------------------- private --------------------------------

    /**
     * 删除缓存信息
     */
    private void removeCache(Serializable id) {
        String cacheKey = this.getCacheKey(id);
        Cache cache = this.getCache();
        if (cache == null) {
            return;
        }
        cache.evict(cacheKey);
    }

    /**
     * 将实体信息保存到缓存中
     */
    private void putCache(Serializable id, V entity) {
        String cacheKey = this.getCacheKey(id);
        Cache cache = this.getCache();
        if (cache == null || entity == null) {
            return;
        }
        cache.put(cacheKey, entity);
    }

    /**
     * 根据主键查询对应的实体信息
     */
    private V getByDB(Serializable id) {
        return this.getBaseMapper().selectById(id);
    }

    /**
     * 根据id从缓存中查询对应的实体信息
     */
    private V getByCache(Serializable id) {
        String cacheKey = this.getCacheKey(id);
        Cache cache = this.getCache();
        if (cache == null) {
            return null;
        }
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper == null) {
            return null;
        }
        return (V) valueWrapper.get();
    }

    /**
     * 生成对应的缓存key
     */
    private String getCacheKey(Serializable id) {
        return String.format(this.getKeyFormat(), id);
    }

}
