package com.imooc.pan.server;

import com.imooc.pan.core.constants.RPanConstants;
import com.imooc.pan.core.response.R;
import io.swagger.annotations.Api;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@SpringBootApplication(scanBasePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH)
@RestController
@Api("测试接口类")
@Validated
@EnableTransactionManagement
@MapperScan(basePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH+".server.modules.**.mapper")
public class RPanServerLauncher {

    public static void main(String[] args) {
        SpringApplication.run(RPanServerLauncher.class, args);
    }

    @GetMapping("hello")
    public R<String> hello(@NotBlank(message = "name不能为空") String name) {
        System.out.println(Thread.currentThread().getContextClassLoader());
        return R.success("hello world, " + name);
    }

}
