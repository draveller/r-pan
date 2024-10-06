package com.imooc.pan.server.modules.share.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;
import com.imooc.pan.web.serializer.Date2StringSerializer;
import com.imooc.pan.web.serializer.IdEncryptSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Schema(name = "分享详情的返回实体对象")
@Data
public class ShareDetailVO implements Serializable {

    private static final long serialVersionUID = -2446579294335071804L;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @Schema(name = "分享的ID")
    private Long shareId;

    @Schema(name = "分享的名称")
    private String shareName;

    @Schema(name = "分享的创建时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date createTime;

    @Schema(name = "分享的过期类型")
    private Integer shareDay;

    @Schema(name = "分享的截止时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date shareEndTime;

    @Schema(name = "分享的文件列表")
    private List<RPanUserFileVO> userFileVOList;

    @Schema(name = "分享者的信息")
    private ShareUserInfoVO shareUserInfoVO;

}
