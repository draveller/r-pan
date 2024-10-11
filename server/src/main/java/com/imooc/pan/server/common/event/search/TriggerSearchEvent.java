package com.imooc.pan.server.common.event.search;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class TriggerSearchEvent extends ApplicationEvent {

    private String keyword;

    private Long userId;

    public TriggerSearchEvent(Object source, String keyword, Long userId) {
        super(source);
        this.keyword = keyword;
        this.userId = userId;
    }

}
