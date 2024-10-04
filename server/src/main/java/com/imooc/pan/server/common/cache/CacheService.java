package com.imooc.pan.server.common.cache;

import java.io.Serializable;

/**
 * 支持业务缓存的顶级Service接口
 *
 * @param <V>
 */
public interface CacheService<V> {

    /**
     * 根据id查询实体信息
     *
     * @param id
     * @return
     */
    V getById(Serializable id);

    /**
     * 根据id更新缓存信息
     *
     * @param id
     * @param entity
     * @return
     */
    boolean updateById(Serializable id, V entity);

    /**
     * 根据id删除缓存信息
     *
     * @param id
     * @return
     */
    boolean removeById(Serializable id);

}
