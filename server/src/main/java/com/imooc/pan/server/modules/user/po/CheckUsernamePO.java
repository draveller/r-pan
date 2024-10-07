package com.imooc.pan.server.modules.user.po;

import com.imooc.pan.server.modules.user.constants.UserConsts;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 校验用户名称PO对象
 */
@Data
@Schema(name = "用户忘记密码-校验用户名参数")
public class CheckUsernamePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = UserConsts.USERNAME_REGEXP, message = "请输入2-16位, 只包含中英文, 数字和下划线的用户名")
    private String username;

}
