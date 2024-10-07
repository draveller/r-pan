package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

@Schema(name ="批量删除文件入参对象实体")
@Data
public class DeleteFilePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "要删除的文件id, 使用分隔符分隔",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "要删除的文件id不能为空")
    private String fileIds;

}
