package com.imooc.pan.server.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.web.serializer.Date2StringSerializer;
import com.imooc.pan.web.serializer.IdEncryptSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Schema(name = "分享链接列表结果实体对象")
@Data
public class RPanShareUrlListVO implements Serializable {

    private static final long serialVersionUID = -5301645564554502650L;

    @Schema(name = "分享的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @Schema(name = "分享的名称")
    private String shareName;

    @Schema(name = "分享的URL")
    private String shareUrl;

    @Schema(name = "分享的分享码")
    private String shareCode;

    @Schema(name = "分享的状态")
    private Integer shareStatus;

    @Schema(name = "分享的类型")
    private Integer shareType;

    @Schema(name = "分享的过期类型")
    private Integer shareDayType;

    @Schema(name = "分享的过期时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date shareEndTime;

    @Schema(name = "分享的创建时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date createTime;

}
