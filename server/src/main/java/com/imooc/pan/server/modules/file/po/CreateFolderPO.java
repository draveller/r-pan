package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "创建文件夹参数实体")
@Data
public class CreateFolderPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "加密的父文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;

    @Schema(name = "文件夹名称")
    @NotBlank(message = "文件夹名称不能为空")
    private String folderName;

}
