package com.imooc.pan.server.modules.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("查询用户已上传的文件分片列表返回实体")
public class UploadedChunksVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "已上传的文件分片的序号列表")
    private List<Integer> uploadedChunks;

}
