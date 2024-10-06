package com.imooc.pan.server.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.web.serializer.IdEncryptSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(name = "文件夹树节点实体")
@Data
public class FolderTreeNodeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(name = "文件夹名称", required = true)
    private String label;

    @Schema(name = "文件夹ID", required = true)
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @Schema(name = "父文件夹ID", required = true)
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @Schema(name = "子节点集合", required = true)
    private List<FolderTreeNodeVO> children;

}
