package com.imooc.pan.server.modules.user.service.cache;

import com.imooc.pan.cache.core.constants.CacheConstants;
import com.imooc.pan.server.common.cache.AnnotationCacheService;
import com.imooc.pan.server.modules.user.entity.RPanUser;
import com.imooc.pan.server.modules.user.mapper.RPanUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 用户模块缓存业务处理类
 */
@Component("userAnnotationCacheService")
public class UserCacheService implements AnnotationCacheService<RPanUser> {

    @Autowired
    private RPanUserMapper rPanUserMapper;

    @Cacheable(cacheNames = CacheConstants.R_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator", sync = true)
    @Override
    public RPanUser getById(Serializable id) {
        return this.rPanUserMapper.selectById(id);
    }

    @CachePut(cacheNames = CacheConstants.R_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean updateById(Serializable id, RPanUser entity) {
        return this.rPanUserMapper.updateById(entity) > 0;
    }

    @CacheEvict(cacheNames = CacheConstants.R_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean removeById(Serializable id) {
        return this.rPanUserMapper.deleteById(id) > 0;
    }
}
