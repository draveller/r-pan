package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Schema(name = "文件分片合并参数对象")
public class FileChunkMergePO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(name = "文件名", required = true)
    @NotBlank(message = "文件名不能为空")
    private String filename;

    @Schema(name = "文件唯一标识", required = true)
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    @Schema(name = "文件总大小", required = true)
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @Schema(name = "父文件夹ID", required = true)
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;
}
