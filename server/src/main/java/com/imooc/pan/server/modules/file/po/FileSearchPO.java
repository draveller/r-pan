package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "文件搜索参数实体")
@Data
public class FileSearchPO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "搜索的关键字", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "搜索的关键字不能为空")
    private String keyword;

    @Schema(name = "文件类型, 使用分隔符拼接")
    private String fileTypes;

}
