package com.imooc.pan.server.common.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

/**
 * 项目启动成功日志打印监听器
 */
@Log4j2
@Component
public class StartedListener implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 项目启动成功将会在日志中输出对应的启动信息
     *
     * @param applicationReadyEvent
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        String serverPort = applicationReadyEvent.getApplicationContext().getEnvironment().getProperty("server.port");
        String serverUrl = String.format("http://%s:%s", "127.0.0.1", serverPort);
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "server started successfully at: " + serverUrl));


        ConfigurableEnvironment env = applicationReadyEvent.getApplicationContext().getEnvironment();
        Boolean springdocEnabled = env.getProperty("springdoc.api-docs.enabled", Boolean.class, true);
        String springdocPath = env.getProperty("springdoc.api-docs.path", String.class, "/v3/api-docs");

        if (Boolean.TRUE.equals(springdocEnabled)) {
            String springdocMsg = "springdoc started successfully at: " + serverUrl + springdocPath;
            log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, springdocMsg));
            String springdocUIMsg = "springdoc ui started successfully at: " + serverUrl + "/swagger-ui/index.html";
            log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, springdocUIMsg));
        }

    }

}
