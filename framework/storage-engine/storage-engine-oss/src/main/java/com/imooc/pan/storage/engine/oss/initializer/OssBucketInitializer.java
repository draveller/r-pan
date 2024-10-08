package com.imooc.pan.storage.engine.oss.initializer;

import com.aliyun.oss.OSSClient;
import com.imooc.pan.core.exception.RPanFrameworkException;
import com.imooc.pan.storage.engine.oss.config.OssStorageEngineConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * OSS桶初始化器
 */
@Slf4j
@Component
public class OssBucketInitializer implements CommandLineRunner {

    @Resource
    private OssStorageEngineConfig config;

    @Resource
    private OSSClient client;

    @Override
    public void run(String... args) throws Exception {
        boolean bucketExist = client.doesBucketExist(config.getBucketName());
        boolean autoCreateBucket = Boolean.TRUE.equals(config.getAutoCreateBucket());

        if (!bucketExist && autoCreateBucket) {
            client.createBucket(config.getBucketName());
        }
        if (!bucketExist && !autoCreateBucket) {
            throw new RPanFrameworkException("the bucket " + config.getBucketName() + " is not available");
        }

        log.info("the bucket {} have been created!", config.getBucketName());
    }

}
