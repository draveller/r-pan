package com.imooc.pan.server.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 文件(夹)删除事件
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class LogicalDeleteFileEvent extends ApplicationEvent {

    private List<Long> fileIdList;

    public LogicalDeleteFileEvent(Object source, List<Long> fileIdList) {
        super(source);
        this.fileIdList = fileIdList;
    }

}
