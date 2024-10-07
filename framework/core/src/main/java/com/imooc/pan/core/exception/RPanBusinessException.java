package com.imooc.pan.core.exception;

import com.imooc.pan.core.response.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义全局业务异常类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RPanBusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    public RPanBusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getDesc();
    }

    public RPanBusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public RPanBusinessException(String message) {
        this.code = ResponseCode.ERROR.getCode();
        this.message = message;
    }

    public RPanBusinessException() {
        this.code = ResponseCode.ERROR.getCode();
        this.message = ResponseCode.ERROR.getDesc();
    }

}
