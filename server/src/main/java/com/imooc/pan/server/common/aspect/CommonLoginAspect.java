package com.imooc.pan.server.common.aspect;

import com.imooc.pan.cache.core.constants.CacheConsts;
import com.imooc.pan.core.response.R;
import com.imooc.pan.core.response.ResponseCode;
import com.imooc.pan.core.utils.JwtUtil;
import com.imooc.pan.server.common.annotation.LoginIgnore;
import com.imooc.pan.server.common.utils.UserIdUtil;
import com.imooc.pan.server.modules.user.constants.UserConsts;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 统一的登录拦截校验切面
 */
@Component
@Aspect
@Slf4j
public class CommonLoginAspect {

    /**
     * 登录认证参数名称
     */
    private static final String LOGIN_AUTH_PARAM_NAME = "authorization";

    /**
     * 请求头登录认证key
     */
    private static final String LOGIN_AUTH_REQUEST_HEADER_NAME = "Authorization";

    /**
     * 切点表达式
     */
    private static final String POINT_CUT = "execution(* com.imooc.pan.server.modules.*.controller..*(..))";

    @Resource
    private CacheManager cacheManager;

    /**
     * 切点模板方法
     */
    @Pointcut(POINT_CUT)
    public void loginAuth() {
    }

    /**
     * 切点的环绕增强逻辑
     * 1. 判断是否需要校验登录信息
     * 2. 校验登录信息: 获取token -> 从缓存中获取token进行比对 -> 解析token -> 将解析的userId存入线程上下文
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("loginAuth()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        if (!this.judgeIfNeedCheckLogin(proceedingJoinPoint)) {
            return proceedingJoinPoint.proceed();
        }

        // 登录信息校验
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String requestUri = request.getRequestURI();
        log.info("成功拦截到请求, uri={}", requestUri);

        if (!this.checkAndSaveUserId(request)) {
            log.info("成功拦截到请求, uri={}, 检测到用户未登录, 将跳转至登陆页面", requestUri);
            return R.fail(ResponseCode.NEED_LOGIN);
        }
        log.info("成功拦截到请求, uri={}, 请求通过", requestUri);

        return proceedingJoinPoint.proceed();
    }


    // ******************************** private ********************************

    /**
     * 校验token并提取userId
     *
     * @param request
     * @return
     */
    private boolean checkAndSaveUserId(HttpServletRequest request) {
        String accessToken = request.getHeader(LOGIN_AUTH_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter(LOGIN_AUTH_PARAM_NAME);
        }
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }

        Object userId = JwtUtil.analyzeToken(accessToken, UserConsts.LOGIN_USER_ID);
        if (userId == null) {
            return false;
        }

        Cache cache = cacheManager.getCache(CacheConsts.R_PAN_CACHE_NAME);
        String redisAccessToken = cache.get(UserConsts.USER_LOGIN_PREFIX + userId, String.class);
        if (StringUtils.isBlank(redisAccessToken)) {
            return false;
        }

        if (Objects.equals(accessToken, redisAccessToken)) {
            this.saveUserId(userId);
            return true;
        }
        return false;

    }

    private void saveUserId(Object userId) {
        UserIdUtil.set(Long.valueOf(String.valueOf(userId)));
    }

    /**
     * @param proceedingJoinPoint
     * @return ture 表示需要校验登录信息, false表示不需要
     */
    private boolean judgeIfNeedCheckLogin(ProceedingJoinPoint proceedingJoinPoint) {
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        return !method.isAnnotationPresent(LoginIgnore.class);
    }

}
