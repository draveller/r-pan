package com.imooc.pan.server.modules.user.context;

import com.imooc.pan.server.modules.user.entity.RPanUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册业务的实体对象
 */
@Data
public class UserLoginContext implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户实体对象
     */
    private RPanUser entity;

    /**
     * 登录成功之后的token
     */
    private String accessToken;

}
