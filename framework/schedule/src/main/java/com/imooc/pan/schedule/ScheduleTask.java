package com.imooc.pan.schedule;

public interface ScheduleTask extends Runnable {

    /**
     * 获取定时任务的名称
     */
    String getName();

}
