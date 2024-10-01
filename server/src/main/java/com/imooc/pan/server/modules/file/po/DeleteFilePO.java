package com.imooc.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ApiModel(value = "批量删除文件入参对象实体")
@Data
public class DeleteFilePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "要删除的文件id, 使用分隔符分隔",required = true)
    @NotBlank(message = "要删除的文件id不能为空")
    private String fileIds;

}
