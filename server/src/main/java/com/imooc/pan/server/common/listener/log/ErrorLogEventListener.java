package com.imooc.pan.server.common.listener.log;

import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.server.common.event.log.ErrorLogEvent;
import com.imooc.pan.server.modules.log.entity.RPanErrorLog;
import com.imooc.pan.server.modules.log.service.IErrorLogService;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 系统错误日志监听器
 */
@Component
public class ErrorLogEventListener {

    @Resource
    private IErrorLogService iErrorLogService;

    /**
     * 监听系统错误日志事件, 并保存到数据库中
     */
    @EventListener(ErrorLogEvent.class)
    public void saveErrorLog(ErrorLogEvent event) {
        RPanErrorLog errorLog = new RPanErrorLog();
        errorLog.setId(IdUtil.get());
        errorLog.setLogContent(event.getErrorMsg());
        errorLog.setLogStatus(0);
        LocalDateTime now = LocalDateTime.now();
        errorLog.setCreateTime(now);
        errorLog.setUpdateTime(now);
        errorLog.setCreateUser(event.getUserId());
        errorLog.setUpdateUser(event.getUserId());
        this.iErrorLogService.save(errorLog);
    }


}
