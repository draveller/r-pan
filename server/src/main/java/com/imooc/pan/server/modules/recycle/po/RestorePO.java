package com.imooc.pan.server.modules.recycle.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Schema(name = "还原已删除文件参数实体")
public class RestorePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "要还原的文件id集合, 使用分隔符分隔", required = true)
    @NotBlank(message = "要还原的文件id集合不能为空")
    private String fileIds;

}
