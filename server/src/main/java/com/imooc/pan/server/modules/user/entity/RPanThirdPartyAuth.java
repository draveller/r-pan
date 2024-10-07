package com.imooc.pan.server.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息表
 *
 * @TableName r_pan_third_party_auth
 */
@TableName(value = "r_pan_third_party_auth")
@Data
public class RPanThirdPartyAuth implements Serializable {

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 关联的用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 第三方平台名称
     * @see com.imooc.pan.server.modules.user.enums.ThirdPartyProviderEnum
     */
    @TableField(value = "provider")
    private String provider;

    /**
     * 第三方平台用户唯一标识符
     */
    @TableField(value = "provider_uid")
    private String providerUid;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}