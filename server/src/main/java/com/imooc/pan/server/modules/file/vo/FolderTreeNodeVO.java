package com.imooc.pan.server.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.web.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("文件夹树节点实体")
@Data
public class FolderTreeNodeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件夹名称", required = true)
    private String label;

    @ApiModelProperty(value = "文件夹ID", required = true)
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @ApiModelProperty(value = "父文件夹ID", required = true)
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @ApiModelProperty(value = "子节点集合", required = true)
    private List<FolderTreeNodeVO> children;

}
