package com.imooc.pan.server.modules.recycle.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Schema(name = "文件删除参数实体")
@Data
public class DeletePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "要删除的文件ID集合，多个使用公用分割符分隔", required = true)
    @NotBlank(message = "请选择要删除的文件")
    private String fileIds;

}
