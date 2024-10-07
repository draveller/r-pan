package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "查询用户已上传分片列表的参数实体")
public class QueryUploadedChunksPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "文件的唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;

}
