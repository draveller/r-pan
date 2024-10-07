package com.imooc.pan.server.modules.user.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 注册用户参数实体对象
 */
@Data
public class UserLoginByGithubContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String code;

}
