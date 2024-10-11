package com.imooc.pan.server.common.schedule.launcher;

import com.imooc.pan.schedule.ScheduleManager;
import com.imooc.pan.server.common.schedule.task.RebuildShareSimpleDetailBloomFilterTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitShareSimpleDetailBloomTaskLauncher implements CommandLineRunner {

    private static final String CRON = "1 0 0 * * ? ";

    @Resource
    private RebuildShareSimpleDetailBloomFilterTask task;

    @Resource
    private ScheduleManager scheduleManager;

    @Override
    public void run(String... args) {
        scheduleManager.startTask(task, CRON);
    }

}
