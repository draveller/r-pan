package com.imooc.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ApiModel(value = "文件文件重命名参数实体")
@Data
public class UpdateFilenamePO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件ID", required = true)
    @NotBlank(message = "文件ID不能为空")
    private String fileId;

    @ApiModelProperty(value = "新文件名", required = true)
    @NotBlank(message = "新文件名不能为空")
    private String newFilename;

}
