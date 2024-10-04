package com.imooc.pan.server.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "com.imooc.pan.server")
public class PanServerConfig {


    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = 1;

    /**
     * 分享链接的前缀
     */
    private String sharePrefix = "http://127.0.0.1:8080/share/";

}
