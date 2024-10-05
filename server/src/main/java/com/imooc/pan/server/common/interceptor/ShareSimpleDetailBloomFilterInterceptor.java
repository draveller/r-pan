package com.imooc.pan.server.common.interceptor;

import com.imooc.pan.bloom.filter.core.BloomFilter;
import com.imooc.pan.bloom.filter.core.BloomFilterManager;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.response.ResponseCode;
import com.imooc.pan.core.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 查询简单分享详情布隆过滤器拦截器
 */
@Slf4j
@Component
public class ShareSimpleDetailBloomFilterInterceptor implements BloomFilterInterceptor {

    @Autowired
    private BloomFilterManager manager;

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";

    @Override
    public String getName() {
        return "ShareSimpleDetailBloomFilterInterceptor";
    }

    @Override
    public String[] getPathPatterns() {
        return ArrayUtils.toArray("/share/simple");
    }

    @Override
    public String[] getExcludePatterns() {
        return new String[0];
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String encShareId = request.getParameter("shareId");
        if (StringUtils.isBlank(encShareId)) {
            throw new RPanBusinessException("分享ID不能为空");
        }

        BloomFilter<Object> bloomFilter = manager.getFilter(BLOOM_FILTER_NAME);
        if (bloomFilter == null) {
            log.info("the bloomFilter named {} is null, give up existence judgment... ", BLOOM_FILTER_NAME);
            return true;
        }

        Long shareId = IdUtil.decrypt(encShareId);
        if (bloomFilter.mightContain(shareId)) {
            log.info("the bloomFilter named {} judge shareId {} mightContain pass..", BLOOM_FILTER_NAME, shareId);
            return true;
        }

        log.info("the bloomFilter named {} judge shareId {} mightContain fail..", BLOOM_FILTER_NAME, shareId);
        throw new RPanBusinessException(ResponseCode.SHARE_CANCELLED);
    }

}
