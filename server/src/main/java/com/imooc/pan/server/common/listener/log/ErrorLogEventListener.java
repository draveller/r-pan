package com.imooc.pan.server.common.listener.log;

import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.server.common.event.log.ErrorLogEvent;
import com.imooc.pan.server.modules.log.entity.RPanErrorLog;
import com.imooc.pan.server.modules.log.service.IErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 系统错误日志监听器
 */
@Component
public class ErrorLogEventListener {

    @Autowired
    private IErrorLogService iErrorLogService;

    /**
     * 监听系统错误日志事件, 并保存到数据库中
     *
     * @param event
     */
    @EventListener(ErrorLogEvent.class)
    public void saveErrorLog(ErrorLogEvent event) {
        RPanErrorLog record = new RPanErrorLog();
        record.setId(IdUtil.get());
        record.setLogContent(event.getErrorMsg());
        record.setLogStatus(0);
        LocalDateTime now = LocalDateTime.now();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        record.setCreateUser(event.getUserId());
        record.setUpdateUser(event.getUserId());
        this.iErrorLogService.save(record);
    }


}
