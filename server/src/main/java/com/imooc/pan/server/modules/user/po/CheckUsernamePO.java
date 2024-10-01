package com.imooc.pan.server.modules.user.po;

import com.imooc.pan.server.modules.user.constants.UserConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 校验用户名称PO对象
 */
@Data
@ApiModel(value = "用户忘记密码-校验用户名参数")
public class CheckUsernamePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = UserConstants.USERNAME_REGEXP, message = "请输入2-16位, 只包含中英文, 数字和下划线的用户名")
    private String username;

}
