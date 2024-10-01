package com.imooc.pan.server.modules.user.context;

import lombok.Data;

import java.io.Serializable;

/**
 * 重置用户密码上下文对象
 */
@Data
public class ResetPasswordContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private String token;

}
