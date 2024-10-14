package com.imooc.pan.server.modules.log.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 错误日志表
 *
 * @TableName r_pan_error_log
 */
@TableName(value = "r_pan_error_log")
@Data
public class RPanErrorLog implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 日志内容
     */
    private String logContent;
    /**
     * 日志状态：0 未处理 1 已处理
     */
    private Integer logStatus;
    /**
     * 创建人
     */
    private Long createUser;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新人
     */
    private Long updateUser;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}