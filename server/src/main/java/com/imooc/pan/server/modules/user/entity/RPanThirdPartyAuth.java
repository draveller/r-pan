package com.imooc.pan.server.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息表
 *
 * @TableName r_pan_third_party_auth
 */
@Data
@TableName(value = "r_pan_third_party_auth")
public class RPanThirdPartyAuth implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 关联的用户id
     */
    private Long userId;

    /**
     * 第三方平台名称
     *
     * @see com.imooc.pan.server.modules.user.enums.ThirdPartyProviderEnum
     */
    private String provider;

    /**
     * 第三方平台用户唯一标识符
     */
    private String providerUid;

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