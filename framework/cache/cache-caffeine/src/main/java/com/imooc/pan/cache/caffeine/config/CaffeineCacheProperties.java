package com.imooc.pan.cache.caffeine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Caffeine cache自定义属性配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.imooc.pan.cache.caffeine")
public class CaffeineCacheProperties {

    /**
     * 缓存初始容量
     */
    private Integer initCacheCapacity = 256;

    /**
     * 缓存最大容量, 超过之后会按照 recently or very often (最近最少策略)进行缓存剔除
     */
    private Long maxCacheCapacity = 1_0000L;

    /**
     * 是否允许空值null作为缓存的value
     */
    private Boolean allowNullValue = Boolean.TRUE;

}
