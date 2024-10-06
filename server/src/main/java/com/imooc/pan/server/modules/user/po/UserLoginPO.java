package com.imooc.pan.server.modules.user.po;

import com.imooc.pan.server.modules.user.constants.UserConstants;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 注册用户参数实体对象
 */
@Data
@Schema(name ="用户登录参数")
public class UserLoginPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = UserConstants.USERNAME_REGEXP, message = "请输入2-16位, 只包含中英文, 数字和下划线的用户名")
    private String username;

    @Schema(name = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "请输入6-16位的密码")
    private String password;

}
