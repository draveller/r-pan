package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Schema(name = "查询用户已上传分片列表的参数实体")
public class QueryUploadedChunksPO implements Serializable {

    private static final long serialVersionUID = -1L;

    @Schema(name = "文件的唯一标识", required = true)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;

}
