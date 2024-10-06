package com.imooc.pan.web.exception;

import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.exception.RPanFrameworkException;
import com.imooc.pan.core.response.R;
import com.imooc.pan.core.response.ResponseCode;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(RPanBusinessException.class)
    public R<?> rPanBusinessHandler(RPanBusinessException e) {
        return R.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> methodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().stream().findFirst().get();
        return R.fail(ResponseCode.ERROR_PARAM.getCode(), objectError.getDefaultMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public R<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().get();
        return R.fail(ResponseCode.ERROR_PARAM.getCode(), constraintViolation.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        return R.fail(ResponseCode.ERROR_PARAM);
    }

    @ExceptionHandler(IllegalStateException.class)
    public R<?> illegalStateExceptionHandler(IllegalStateException e) {
        return R.fail(ResponseCode.ERROR_PARAM);
    }

    @ExceptionHandler(BindException.class)
    public R<?> bindExceptionHandler(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().get();
        return R.fail(ResponseCode.ERROR_PARAM.getCode(), fieldError.getDefaultMessage());
    }

    @ExceptionHandler(RPanFrameworkException.class)
    public R<?> rPanFrameworkExceptionHandler(RPanFrameworkException e) {
        return R.fail(ResponseCode.ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public R<?> runtimeExceptionHandler(RuntimeException e) {
        e.printStackTrace();
        return R.fail(ResponseCode.ERROR.getCode(), e.getMessage());
    }

}
