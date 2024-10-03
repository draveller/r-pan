package com.imooc.pan.storage.engine.local.config;


import com.imooc.pan.core.utils.FileUtils;
import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@SpringBootConfiguration
@ConfigurationProperties(prefix = "com.imooc.pan.storage.engine.local")
public class LocalStorageEngineConfig {

    /**
     * 实际存放路径的前缀
     */
    private String rootFilePath = FileUtils.generateDefaultStoreFileRealPath();

    /**
     * 实际存放文件分片路径的前缀
     */
    private String rootFileChunkPath = FileUtils.generateDefaultStoreFileChunkRealPath();

}
