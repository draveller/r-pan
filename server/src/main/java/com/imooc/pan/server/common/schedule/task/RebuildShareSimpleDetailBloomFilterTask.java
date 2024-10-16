package com.imooc.pan.server.common.schedule.task;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.imooc.pan.bloom.filter.core.BloomFilter;
import com.imooc.pan.bloom.filter.core.BloomFilterManager;
import com.imooc.pan.schedule.ScheduleTask;
import com.imooc.pan.server.modules.share.service.ShareService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 定时重建简单分享详情布隆过滤器任务
 */
@Component
@Slf4j
public class RebuildShareSimpleDetailBloomFilterTask implements ScheduleTask {

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";
    @Resource
    private BloomFilterManager manager;
    @Resource
    private ShareService shareService;

    /**
     * 获取定时任务的名称
     */
    @Override
    public String getName() {
        return "RebuildShareSimpleDetailBloomFilterTask";
    }

    /**
     * 执行重建任务
     */
    @Override
    public void run() {
        log.info("start rebuild ShareSimpleDetailBloomFilter...");

        BloomFilter<Long> filter = manager.getFilter(BLOOM_FILTER_NAME);
        if (filter == null) {
            log.info("the bloomFilter named {} is null, give up rebuild...", BLOOM_FILTER_NAME);
            return;
        }
        filter.clear();

        long startId = 0L;
        long limit = 1_0000L;
        AtomicLong addCount = new AtomicLong(0L);
        List<Long> shareIdList;
        do {
            shareIdList = this.shareService.rollingQueryShareId(startId, limit);
            if (CollectionUtils.isNotEmpty(shareIdList)) {
                for (Long shareId : shareIdList) {
                    filter.put(shareId);
                    addCount.incrementAndGet();
                }
                startId = shareIdList.get(shareIdList.size() - 1);
            }
        } while (CollectionUtils.isNotEmpty(shareIdList));

        log.info("finish rebuild ShareSimpleDetailBloomFilter, total set item count {} ...", addCount.get());
    }


}
