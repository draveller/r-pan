package com.imooc.pan.server.modules.user.context;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 注册用户参数实体对象
 */
@Data
public class UserLoginByGithubContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

}
