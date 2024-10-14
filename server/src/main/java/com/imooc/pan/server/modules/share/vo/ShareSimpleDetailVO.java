package com.imooc.pan.server.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.web.serializer.IdEncryptSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "查询分享简单详情返回实体对象")
@Data
public class ShareSimpleDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "分享ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @Schema(name = "分享名称")
    private String shareName;

    @Schema(name = "分享人信息")
    private ShareUserInfoVO shareUserInfoVO;

}
