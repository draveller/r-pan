package com.imooc.pan.server.modules.user.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * 重置用户密码PO对象
 */
@Data
@Schema(name ="用户在线修改密码参数")
public class ChangePasswordPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "旧密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "旧密码不能为空")
    @Length(min = 6, max = 16, message = "请输入6-16位的旧密码")
    private String oldPassword;

    @Schema(name = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码不能为空")
    @Length(min = 6, max = 16, message = "请输入6-16位的新密码")
    private String newPassword;

}
