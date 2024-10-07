package com.imooc.pan.server.modules.share.po;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "创建分享链接的参数对象实体")
@Data
public class CreateShareUrlPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "分享的名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分享名称不能为空")
    private String shareName;

    @Schema(name = "分享的类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分享的类型不能为空")
    private Integer shareType;

    @Schema(name = "分享的日期类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分享的日期类型不能为空")
    private Integer shareDayType;

    @Schema(name = "分享的文件ID集合，多个使用公用的分割符去拼接", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分享的文件ID不能为空")
    private String shareFileIds;

}
