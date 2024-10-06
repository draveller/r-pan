package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Schema(name = "文件转移参数实体对象")
public class TransferFilePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "要转移的文件ID集合, 多个使用分隔符分隔", required = true)
    @NotBlank(message = "要转移的文件ID集合不能为空")
    private String fileIds;

    @Schema(name = "目标文件夹ID", required = true)
    @NotBlank(message = "目标文件夹ID不能为空")
    private String targetParentId;

}
