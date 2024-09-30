package com.imooc.pan.schedule.test.config;

import com.imooc.pan.core.constants.RPanConstants;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan(RPanConstants.BASE_COMPONENT_SCAN_PATH+".schedule")
public class ScheduleTestConfig {


}
