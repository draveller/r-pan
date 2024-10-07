package com.imooc.pan.server.modules.share.po;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "校验分享码参数实体对象")
@Data
public class CheckShareCodePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "分享的ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分享ID不能为空")
    private String shareId;

    @Schema(name = "分享的分享码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分享的分享码不能为空")
    private String shareCode;

}
