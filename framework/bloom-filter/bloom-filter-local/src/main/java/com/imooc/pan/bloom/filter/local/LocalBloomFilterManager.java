package com.imooc.pan.bloom.filter.local;

import com.google.common.collect.Maps;
import com.imooc.pan.bloom.filter.core.BloomFilter;
import com.imooc.pan.bloom.filter.core.BloomFilterManager;
import org.apache.commons.collections.CollectionUtils;
import org.mockito.internal.matchers.Any;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 本地布隆过滤器的管理器
 */
@Component
public class LocalBloomFilterManager implements BloomFilterManager, InitializingBean {

    @Autowired
    private LocalBloomFilterConfig config;

    /**
     * 容器
     */
    private final Map<String, BloomFilter> bloomFilterContainer = Maps.newConcurrentMap();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<LocalBloomFilterConfigItem> items = config.getItems();
        if (CollectionUtils.isNotEmpty(items)) {
            for (LocalBloomFilterConfigItem item : items) {
                String funnelTypeName = item.getFunnelTypeName();
                try {
                    FunnelType funnelType = FunnelType.valueOf(funnelTypeName);
                    bloomFilterContainer.putIfAbsent(item.getName(),
                            new LocalBloomFilter<>(funnelType.getFunnel(), item.getExpectedInsertions(), item.getFpp()));
                } catch (Exception ignored) {
                }

            }
        }
    }

    @Override
    public <T>BloomFilter<T> getFilter(String name) {
        return bloomFilterContainer.get(name);
    }

    @Override
    public Collection<String> getFilterNames() {
        return bloomFilterContainer.keySet();
    }

}
