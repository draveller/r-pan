package com.imooc.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("文件分片合并参数对象")
public class FileChunkMergePO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件名", required = true)
    @NotBlank(message = "文件名不能为空")
    private String filename;

    @ApiModelProperty(value = "文件唯一标识", required = true)
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    @ApiModelProperty(value = "文件总大小", required = true)
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @ApiModelProperty(value = "父文件夹ID", required = true)
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;
}
