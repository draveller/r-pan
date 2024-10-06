package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Schema(name = "单文件上传参数实体对象")
public class FileUploadPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "文件名", required = true)
    @NotBlank(message = "文件名不能为空")
    private String filename;

    @Schema(name = "文件的唯一标识", required = true)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;

    @Schema(name = "文件的总大小", required = true)
    @NotNull(message = "文件的总大小不能为空")
    private Long totalSize;

    @Schema(name = "文件的父文件夹id", required = true)
    @NotBlank(message = "文件的父文件夹id不能为空")
    private String parentId;

    @Schema(name = "文件实体", required = true)
    @NotNull(message = "文件实体不能为空")
    private MultipartFile file;
}
