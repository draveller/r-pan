package com.imooc.pan.server.common.listener.search;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imooc.pan.server.common.event.search.TriggerSearchEvent;
import com.imooc.pan.server.modules.user.entity.RPanUserSearchHistory;
import com.imooc.pan.server.modules.user.service.IUserSearchHistoryService;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户搜索事件监听器
 */
@Component
public class TriggerSearchEventListener {

    @Resource
    private IUserSearchHistoryService iUserSearchHistoryService;

    /**
     * 监听用户搜索事件, 将其保存到用户的搜索历史记录中
     *
     * @param event
     */
    @EventListener(classes = TriggerSearchEvent.class)
    public void saveSearchHistory(TriggerSearchEvent event) {
        RPanUserSearchHistory historyRecord = new RPanUserSearchHistory();
        historyRecord.setUserId(event.getUserId());
        historyRecord.setUserId(event.getUserId());
        historyRecord.setSearchContent(event.getKeyword());
        LocalDateTime now = LocalDateTime.now();
        historyRecord.setCreateTime(now);
        historyRecord.setUpdateTime(now);

        try {
            this.iUserSearchHistoryService.save(historyRecord);
        } catch (DuplicateKeyException e) {
            LambdaUpdateWrapper<RPanUserSearchHistory> wrapper = Wrappers.<RPanUserSearchHistory>lambdaUpdate()
                    .eq(RPanUserSearchHistory::getUserId, event.getUserId())
                    .eq(RPanUserSearchHistory::getSearchContent, event.getKeyword())
                    .set(RPanUserSearchHistory::getUpdateTime, new Date());

            this.iUserSearchHistoryService.update(wrapper);
        }
    }

}
