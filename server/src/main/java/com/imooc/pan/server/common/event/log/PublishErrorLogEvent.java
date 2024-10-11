package com.imooc.pan.server.common.event.log;

import lombok.*;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class PublishErrorLogEvent extends ApplicationEvent {

    /**
     * 错误日志的内容
     */
    private String errorMsg;

    /**
     * 当前登录的用户id
     */
    private Long userId;

    public PublishErrorLogEvent(Object source, String errorMsg, Long userId) {
        super(source);
        this.errorMsg = errorMsg;
        this.userId = userId;
    }

}
