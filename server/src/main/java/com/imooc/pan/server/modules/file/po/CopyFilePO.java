package com.imooc.pan.server.modules.file.po;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "文件复制参数实体对象")
public class CopyFilePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "要复制的文件ID集合, 多个使用分隔符分隔", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "要复制的文件ID集合不能为空")
    private String fileIds;

    @Schema(name = "目标文件夹ID", required = true)
    @NotBlank(message = "目标文件夹ID不能为空")
    private String targetParentId;

}
