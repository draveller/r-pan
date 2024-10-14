package com.imooc.pan.server.common.launcher;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.imooc.pan.bloom.filter.core.BloomFilter;
import com.imooc.pan.bloom.filter.core.BloomFilterManager;
import com.imooc.pan.server.modules.share.service.ShareService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 简单分享详情布隆过滤器初始化器
 */
@Slf4j
@Component
public class InitShareSimpleDetailLauncher implements CommandLineRunner {

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";
    @Resource
    private BloomFilterManager manager;
    @Resource
    private ShareService shareService;

    @Override
    public void run(String... args) throws Exception {
        log.info("start init ShareSimpleDetailBloomFilter...");

        BloomFilter<Long> filter = manager.getFilter(BLOOM_FILTER_NAME);
        if (filter == null) {
            log.info("the bloomFilter named {} is null, give up init...", BLOOM_FILTER_NAME);
            return;
        }
        filter.clear();

        long startId = 0L;
        long limit = 1_0000L;
        AtomicLong addCount = new AtomicLong(0L);
        List<Long> shareIdList;
        do {
            shareIdList = shareService.rollingQueryShareId(startId, limit);
            if (CollectionUtils.isNotEmpty(shareIdList)) {
                for (Long shareId : shareIdList) {
                    filter.put(shareId);
                    addCount.incrementAndGet();
                }
                startId = shareIdList.get(shareIdList.size() - 1);
            }
        } while (CollectionUtils.isNotEmpty(shareIdList));

        log.info("finish init ShareSimpleDetailBloomFilter, total set item count {} ...", addCount.get());
    }

}
