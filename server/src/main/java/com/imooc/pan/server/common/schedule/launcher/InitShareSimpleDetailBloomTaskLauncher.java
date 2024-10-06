package com.imooc.pan.server.common.schedule.launcher;

import com.imooc.pan.schedule.ScheduleManager;
import com.imooc.pan.server.common.schedule.task.RebuildShareSimpleDetailBloomFilterTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitShareSimpleDetailBloomTaskLauncher implements CommandLineRunner {
    private final static String CRON = "1 0 0 * * ? ";


    @Autowired
    private RebuildShareSimpleDetailBloomFilterTask task;

    @Autowired
    private ScheduleManager scheduleManager;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(task, CRON);
    }

}
