package com.imooc.pan.server.modules.user.po;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 注册用户参数实体对象
 */
@Data
@Schema(name = "用户通过github授权登录参数")
public class UserLoginByGithubPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "授权码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "授权码不能为空")
    private String code;

}
