package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Schema(name ="创建文件夹参数实体")
@Data
public class CreateFolderPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "加密的父文件夹ID", required = true)
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;

    @Schema(name = "文件夹名称")
    @NotBlank(message = "文件夹名称不能为空")
    private String folderName;

}
