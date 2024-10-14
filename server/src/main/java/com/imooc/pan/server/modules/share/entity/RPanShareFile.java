package com.imooc.pan.server.modules.share.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户分享文件表
 *
 * @TableName r_pan_share_file
 */
@Data
@TableName(value = "r_pan_share_file")
public class RPanShareFile implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 分享id
     */
    private Long shareId;
    /**
     * 文件记录ID
     */
    private Long fileId;
    /**
     * 分享创建人
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
}