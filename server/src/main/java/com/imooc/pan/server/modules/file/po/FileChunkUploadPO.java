package com.imooc.pan.server.modules.file.po;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "文件分片上传参数实体")
@Data
public class FileChunkUploadPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "文件名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名不能为空")
    private String filename;

    @Schema(name = "文件唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    @Schema(name = "文件分片总数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件分片总数不能为空")
    private Integer totalChunks;

    @Schema(name = "当前分片序号, 从1开始", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "当前分片序号不能为空")
    private Integer chunkNumber;

    @Schema(name = "当前分片大小", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "当前分片大小不能为空")
    private Long currentChunkSize;

    @Schema(name = "文件总大小", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @Schema(name = "文件分片实体", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件分片实体不能为空")
    private transient MultipartFile file;

}
