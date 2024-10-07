package com.imooc.pan.server.modules.share.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "保存至我的网盘参数实体对象")
@Data
public class ShareSavePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "要转存的文件ID集合，多个使用公用分隔符拼接", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要保存的文件")
    private String fileIds;

    @Schema(name = "要转存到的文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要保存到的文件夹")
    private String targetParentId;

}
