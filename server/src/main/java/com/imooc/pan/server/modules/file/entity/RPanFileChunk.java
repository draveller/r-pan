package com.imooc.pan.server.modules.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件分片信息表
 *
 * @TableName r_pan_file_chunk
 */
@Data
@TableName(value = "r_pan_file_chunk")
public class RPanFileChunk implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 分片真实的存储路径
     */
    private String realPath;

    /**
     * 分片编号
     */
    private Integer chunkNumber;

    /**
     * 过期时间
     */
    private LocalDateTime expirationTime;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}