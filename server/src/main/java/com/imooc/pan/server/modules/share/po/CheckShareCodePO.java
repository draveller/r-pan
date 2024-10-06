package com.imooc.pan.server.modules.share.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Schema(name = "校验分享码参数实体对象")
@Data
public class CheckShareCodePO implements Serializable {

    private static final long serialVersionUID = -8829888408230236969L;

    @Schema(name = "分享的ID", required = true)
    @NotBlank(message = "分享ID不能为空")
    private String shareId;

    @Schema(name = "分享的分享码", required = true)
    @NotBlank(message = "分享的分享码不能为空")
    private String shareCode;

}
