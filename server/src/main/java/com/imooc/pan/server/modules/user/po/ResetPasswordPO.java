package com.imooc.pan.server.modules.user.po;

import com.imooc.pan.server.modules.user.constants.UserConsts;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 重置用户密码PO对象
 */
@Data
@Schema(name = "用户忘记密码-重置密码参数")
public class ResetPasswordPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = UserConsts.USERNAME_REGEXP, message = "请输入2-16位, 只包含中英文, 数字和下划线的用户名")
    private String username;

    @Schema(name = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "请输入6-16位的密码")
    private String password;

    @Schema(name = "临时令牌", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "令牌不能为空")
    private String token;

}
