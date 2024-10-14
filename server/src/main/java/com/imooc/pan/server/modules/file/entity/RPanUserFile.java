package com.imooc.pan.server.modules.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户文件信息表
 *
 * @TableName r_pan_user_file
 */
@TableName(value = "r_pan_user_file")
@Data
public class RPanUserFile implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 文件记录ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 上级文件夹ID,顶级文件夹为0
     */
    private Long parentId;
    /**
     * 真实文件id
     */
    private Long realFileId;
    /**
     * 文件名
     */
    private String filename;
    /**
     * 是否是文件夹 （0 否 1 是）
     */
    private Integer folderFlag;
    /**
     * 文件大小展示字符
     */
    private String fileSizeDesc;
    /**
     * 文件类型（1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）
     */
    private Integer fileType;
    /**
     * 删除标识（0 否 1 是）
     */
    private Integer delFlag;
    /**
     * 创建人
     */
    private Long createUser;
    /**
     * 更新人
     */
    private Long updateUser;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}