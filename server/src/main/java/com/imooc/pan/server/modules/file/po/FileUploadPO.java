package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "单文件上传参数实体对象")
public class FileUploadPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "文件名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名不能为空")
    private String filename;

    @Schema(name = "文件的唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;

    @Schema(name = "文件的总大小", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件的总大小不能为空")
    private Long totalSize;

    @Schema(name = "文件的父文件夹id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件的父文件夹id不能为空")
    private String parentId;

    @Schema(name = "文件实体", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件实体不能为空")
    private transient MultipartFile file;
}
