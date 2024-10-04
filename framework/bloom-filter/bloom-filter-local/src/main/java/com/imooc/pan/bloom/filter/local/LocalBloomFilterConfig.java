package com.imooc.pan.bloom.filter.local;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "com.imooc.pan.bloom.filter.local")
public class LocalBloomFilterConfig {

    private List<LocalBloomFilterConfigItem> items;

}
