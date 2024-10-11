package com.imooc.pan.server;

import com.imooc.pan.core.constants.GlobalConst;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan(basePackages = GlobalConst.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
@ServletComponentScan(basePackages = GlobalConst.BASE_COMPONENT_SCAN_PATH)
@SpringBootApplication(scanBasePackages = GlobalConst.BASE_COMPONENT_SCAN_PATH)
@EnableTransactionManagement
public class RPanServerLauncher {

    public static void main(String[] args) {
        SpringApplication.run(RPanServerLauncher.class, args);
    }

}
