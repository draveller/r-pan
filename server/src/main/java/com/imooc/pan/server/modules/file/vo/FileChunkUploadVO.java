package com.imooc.pan.server.modules.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("文件分片上传的响应实体")
@Data
public class FileChunkUploadVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否需要合并文件, 0-需要; 1-不需要")
    private Integer mergeFlag;

}
