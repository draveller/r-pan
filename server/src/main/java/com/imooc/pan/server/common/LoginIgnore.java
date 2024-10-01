package com.imooc.pan.server.common;

import java.lang.annotation.*;

/**
 * 注解在不需要登录的接口上
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LoginIgnore {

}
