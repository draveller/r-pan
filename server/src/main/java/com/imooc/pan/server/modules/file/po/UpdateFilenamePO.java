package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Schema(name ="文件文件重命名参数实体")
@Data
public class UpdateFilenamePO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(name = "文件ID", required = true)
    @NotBlank(message = "文件ID不能为空")
    private String fileId;

    @Schema(name = "新文件名", required = true)
    @NotBlank(message = "新文件名不能为空")
    private String newFilename;

}
