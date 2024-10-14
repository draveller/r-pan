package com.imooc.pan.server.common.annotation;

import java.lang.annotation.*;

/**
 * @see com.imooc.pan.server.common.aspect.CheckLoginAspect
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NoCheckLogin {
}
