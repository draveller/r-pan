package com.imooc.pan.swagger2;

import com.imooc.pan.core.constants.RPanConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * swagger2配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "swagger2")
public class Swagger2ConfigProperties {


    private boolean show = true;

    private String groupName = "r-pan";

    private String basePackage = RPanConstants.BASE_COMPONENT_SCAN_PATH;

    private String title = "r-pan-server";

    private String description = "r-pan-server";

    private String termsOfServiceUrl = "http://127.0.0.1:${server.port}";

    private String contactName = "ding";

    private String contactUrl = "https://draveller.github.io";

    private String contactEmail = "ddraveller@163.com";

    private String version = "1.0";
}
