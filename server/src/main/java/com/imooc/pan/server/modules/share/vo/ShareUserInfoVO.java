package com.imooc.pan.server.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.web.serializer.IdEncryptSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "分享者信息返回实体对象")
@Data
public class ShareUserInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "分享者的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long userId;

    @Schema(name = "分享者的名称")
    private String username;

}
