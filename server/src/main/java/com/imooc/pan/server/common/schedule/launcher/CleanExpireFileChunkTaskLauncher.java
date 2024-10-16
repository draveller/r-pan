package com.imooc.pan.server.common.schedule.launcher;

import com.imooc.pan.schedule.ScheduleManager;
import com.imooc.pan.server.common.schedule.task.CleanExpireChunkFileTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 定时清理过期的文件分片任务触发器
 */
@Slf4j
@Component
public class CleanExpireFileChunkTaskLauncher implements CommandLineRunner {

    private static final String CRON = "1 0 0 * * ? ";

    @Resource
    private CleanExpireChunkFileTask task;

    @Resource
    private ScheduleManager scheduleManager;

    @Override
    public void run(String... args) {
        scheduleManager.startTask(task, CRON);
    }

}
