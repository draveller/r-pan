package com.imooc.pan.server.modules.recycle.po;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "还原已删除文件参数实体")
public class RestorePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "要还原的文件id集合, 使用分隔符分隔", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "要还原的文件id集合不能为空")
    private String fileIds;

}
