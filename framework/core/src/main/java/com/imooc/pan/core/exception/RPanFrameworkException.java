package com.imooc.pan.core.exception;

import lombok.Data;

/**
 * 自定义全局业务异常类
 */
@Data
public class RPanFrameworkException extends RuntimeException {

    public RPanFrameworkException(String message) {
        super(message);
    }
}
