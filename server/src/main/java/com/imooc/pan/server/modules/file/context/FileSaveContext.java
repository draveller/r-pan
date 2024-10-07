package com.imooc.pan.server.modules.file.context;

import com.imooc.pan.server.modules.file.entity.RPanFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * 保存单文件的上下文实体
 */
@Data
public class FileSaveContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件标识
     */
    private String identifier;

    /**
     * 文件大小
     */
    private Long totalSize;

    /**
     * 文件
     */
    private transient MultipartFile file;

    /**
     * 当前用户ID
     */
    private Long userId;

    /**
     * 实体文件记录
     */
    private RPanFile entity;

    /**
     * 文件上传的物理路径
     */
    private String realPath;

}
