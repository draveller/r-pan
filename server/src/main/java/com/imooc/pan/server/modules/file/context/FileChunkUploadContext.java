package com.imooc.pan.server.modules.file.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件分片上传上下文参数实体
 */
@Data
public class FileChunkUploadContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件分片总数
     */
    private Integer totalChunks;

    /**
     * 当前分片序号
     */
    private Integer chunkNumber;

    /**
     * 当前分片大小
     */
    private Long currentChunkSize;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 文件分片实体
     */
    private transient MultipartFile file;

    /**
     * 用户ID
     */
    private Long userId;

}
