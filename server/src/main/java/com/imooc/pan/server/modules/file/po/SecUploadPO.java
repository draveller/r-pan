package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "文件秒传参数实体")
@Data
public class SecUploadPO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "父文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;

    @Schema(name = "文件名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名不能为空")
    private String filename;

    @Schema(name = "文件唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

}
