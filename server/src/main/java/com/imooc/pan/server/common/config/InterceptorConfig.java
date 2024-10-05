package com.imooc.pan.server.common.config;

import com.imooc.pan.server.common.interceptor.BloomFilterInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 拦截器配置类
 */
@Slf4j
@SpringBootConfiguration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private List<BloomFilterInterceptor> interceptorList;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (CollectionUtils.isNotEmpty(interceptorList)) {
            interceptorList.forEach(
                    interceptor -> {
                        registry.addInterceptor(interceptor)
                                .addPathPatterns(interceptor.getPathPatterns())
                                .excludePathPatterns(interceptor.getExcludePatterns());
                        log.info("add bloomFilterInterceptor {} finish", interceptor.getName());
                    });
        }
    }

}
