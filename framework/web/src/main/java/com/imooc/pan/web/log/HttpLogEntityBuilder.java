package com.imooc.pan.web.log;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

/**
 * HttpLogEntity构造器
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpLogEntityBuilder {

    private static final String UNKNOWN_STR = "unknown";

    /**
     * 构建HTTP日志对象
     */
    public static HttpLogEntity build(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper, StopWatch stopWatch) {
        HttpLogEntity httpLogEntity = new HttpLogEntity();
        httpLogEntity.setRequestUri(requestWrapper.getRequestURI())
                .setMethod(requestWrapper.getMethod())
                .setRemoteAddr(requestWrapper.getRemoteAddr())
                .setIp(getIpAddress(requestWrapper))
                .setRequestHeaders(getRequestHeaderMap(requestWrapper));
        if (requestWrapper.getMethod().equals(RequestMethod.GET.name())) {
            httpLogEntity.setRequestParams(JSON.toJSONString(requestWrapper.getParameterMap()));
        } else {
            httpLogEntity.setRequestParams(new String(requestWrapper.getContentAsByteArray()));
        }
        String responseContentType = responseWrapper.getContentType();
        if (StrUtil.equals("application/json;charset=UTF-8", responseContentType)) {
            httpLogEntity.setResponseData(new String(responseWrapper.getContentAsByteArray()));
        } else {
            httpLogEntity.setResponseData("Stream Body...");
        }
        httpLogEntity.setStatus(responseWrapper.getStatus())
                .setResponseHeaders(getResponseHeaderMap(responseWrapper))
                .setResolveTime(stopWatch.toString());
        return httpLogEntity;
    }

    /**
     * 获取IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN_STR;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || UNKNOWN_STR.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN_STR.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN_STR.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN_STR.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.isEmpty() || UNKNOWN_STR.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 获取请求头MAP
     */
    public static Map<String, String> getRequestHeaderMap(HttpServletRequest request) {
        Map<String, String> result = Maps.newHashMap();
        if (Objects.nonNull(request)) {
            Enumeration<String> headerNames = request.getHeaderNames();

            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                result.put(headerName, headerValue);
            }

        }
        return result;
    }

    /**
     * 获取响应头MAP
     */
    public static Map<String, String> getResponseHeaderMap(HttpServletResponse response) {
        Map<String, String> result = Maps.newHashMap();
        if (Objects.nonNull(response)) {
            String contentType = response.getContentType();
            result.put("contentType", contentType);
        }
        return result;
    }

}
