package com.imooc.pan.server.modules.share.po;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "取消分享参数实体对象")
@Data
public class CancelSharePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "要取消的分享ID的集合，多个使用公用的分割符拼接", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要取消的分享")
    private String shareIds;

}
