package com.imooc.pan.server.modules.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.web.serializer.IdEncryptSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(name ="用户基本信息实体")
@Data
public class UserInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(name = "用户名")
    private String username;

    @Schema(name = "用户根目录的加密id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long rootFileId;

    @Schema(name = "用户根目录名称")
    private String rootFileName;

}
