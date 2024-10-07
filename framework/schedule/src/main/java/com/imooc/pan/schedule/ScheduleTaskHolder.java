package com.imooc.pan.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务和定时任务结果的缓存对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTaskHolder implements Serializable {


    /**
     * 执行的任务
     */
    private transient ScheduleTask scheduleTask;

    /**
     * 执行任务的结果
     */
    private transient ScheduledFuture<?> scheduledFuture;

}
