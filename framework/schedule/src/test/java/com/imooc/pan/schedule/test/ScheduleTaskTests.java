package com.imooc.pan.schedule.test;

import com.imooc.pan.schedule.ScheduleManager;
import com.imooc.pan.schedule.test.config.ScheduleTestConfig;
import com.imooc.pan.schedule.test.task.SimpleScheduleTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 定时任务模块单元测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScheduleTestConfig.class)
public class ScheduleTaskTests {

    @Autowired
    private ScheduleManager scheduleManager;

    @Autowired
    private SimpleScheduleTask simpleScheduleTask;

    @Test
    public void test_run_schedule_task() throws InterruptedException {
        String cron = "0/5 * * * * ?";
        String key = scheduleManager.startTask(this.simpleScheduleTask, cron);
        Thread.sleep(10_000);

        cron = "0/1 * * * * ?";
        scheduleManager.changeTask(key, cron);
        Thread.sleep(10_000);

        scheduleManager.stopTask(key);
    }

}
