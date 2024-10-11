package com.imooc.pan.server.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 文件还原事件实体
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class RestoreFileEvent extends ApplicationEvent {

    /**
     * 被成功还原的文件记录ID集合
     */
    private List<Long> fileIdList;

    public RestoreFileEvent(Object source, List<Long> fileIdList) {
        super(source);
        this.fileIdList = fileIdList;
    }

}
