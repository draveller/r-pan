package com.imooc.pan.server.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.web.serializer.IdEncryptSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "创建分享链接的返回实体对象")
@Data
public class RPanShareUrlVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @Schema(name = "分享链接的ID")
    private Long shareId;

    @Schema(name = "分享链接的名称")
    private String shareName;

    @Schema(name = "分享链接的URL")
    private String shareUrl;

    @Schema(name = "分享链接的分享码")
    private String shareCode;

    @Schema(name = "分享链接的状态")
    private Integer shareStatus;

}
