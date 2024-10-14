package com.imooc.pan.storage.engine.oss.config;

import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSSClient;
import com.imooc.pan.core.exception.RPanFrameworkException;
import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * OSS文件存储引擎配置类
 */
@Data
@ConfigurationProperties(prefix = "com.imooc.pan.storage.engine.oss")
@SpringBootConfiguration
public class OssStorageEngineConfig {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

    private Boolean autoCreateBucket;

    /**
     * 注入OSS操作客户端对象
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public OSSClient ossClient() {
        if (StrUtil.isBlank(getEndpoint()) || StrUtil.isBlank(getAccessKeyId())
                || StrUtil.isBlank(getAccessKeySecret()) || StrUtil.isBlank(getBucketName())) {
            throw new RPanFrameworkException("the oss config is missed!");
        }
        return new OSSClient(getEndpoint(), getAccessKeyId(), getAccessKeySecret());
    }


}
