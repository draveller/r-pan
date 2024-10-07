package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "文件文件重命名参数实体")
public class UpdateFilenamePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件ID不能为空")
    private String fileId;

    @Schema(name = "新文件名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新文件名不能为空")
    private String newFilename;

}
