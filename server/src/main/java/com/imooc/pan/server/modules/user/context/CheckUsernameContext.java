package com.imooc.pan.server.modules.user.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 校验用户名称上下文对象
 */
@Data
public class CheckUsernameContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

}
