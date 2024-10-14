package com.imooc.pan.schedule;

import cn.hutool.core.util.IdUtil;
import com.imooc.pan.core.exception.RPanFrameworkException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务管理器
 * 1. 创建并启动一个定时任务
 * 2. 停止
 * 3. 更新
 */
@Component
@Slf4j
public class ScheduleManager {

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * 内部正在执行的定时任务缓存
     */
    private Map<String, ScheduleTaskHolder> cache = new ConcurrentHashMap<>();

    /**
     * 启动一个定时任务
     *
     * @param scheduleTask 定时任务对象
     * @param cron         定时表达式
     * @return 唯一标识符
     */
    public String startTask(ScheduleTask scheduleTask, String cron) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(scheduleTask, new CronTrigger(cron));

        String key = IdUtil.fastSimpleUUID();
        ScheduleTaskHolder holder = new ScheduleTaskHolder(scheduleTask, scheduledFuture);
        cache.put(key, holder);

        log.info("{} 启动成功! 唯一标识为: {}", scheduleTask.getName(), key);
        return key;
    }

    /**
     * 停止一个定时任务
     *
     * @param key 任务id
     */
    public void stopTask(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        ScheduleTaskHolder holder = cache.get(key);
        if (holder == null) {
            return;
        }

        ScheduledFuture<?> scheduledFuture = holder.getScheduledFuture();
        boolean cancel = scheduledFuture.cancel(true);

        if (cancel) {
            cache.remove(key);
            log.info("{} 停止成功! 唯一标识为: {}", holder.getScheduleTask().getName(), key);
        } else {
            log.error("{} 停止失败! 唯一标识为: {}", holder.getScheduleTask().getName(), key);
        }

    }

    /**
     * 更新定时任务
     *
     * @param key  唯一标识
     * @param cron 时间表达式
     * @return 任务id
     */
    public String changeTask(String key, String cron) {
        if (StringUtils.isAnyBlank(key, cron)) {
            throw new RPanFrameworkException("定时任务参数不正确!");
        }
        ScheduleTaskHolder holder = cache.get(key);
        if (holder == null) {
            throw new RPanFrameworkException("定时任务唯一标识不正确!");
        }
        this.stopTask(key);
        return this.startTask(holder.getScheduleTask(), cron);
    }


}
