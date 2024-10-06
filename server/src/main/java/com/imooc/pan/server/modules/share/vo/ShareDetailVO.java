package com.imooc.pan.server.modules.share.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;
import com.imooc.pan.web.serializer.IdEncryptSerializer;
import com.imooc.pan.web.serializer.LocalDateTime2StringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "分享详情的返回实体对象")
@Data
public class ShareDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @Schema(name = "分享的ID")
    private Long shareId;

    @Schema(name = "分享的名称")
    private String shareName;

    @Schema(name = "分享的创建时间")
    @JsonSerialize(using = LocalDateTime2StringSerializer.class)
    private LocalDateTime createTime;

    @Schema(name = "分享的过期类型")
    private Integer shareDay;

    @Schema(name = "分享的截止时间")
    @JsonSerialize(using = LocalDateTime2StringSerializer.class)
    private LocalDateTime shareEndTime;

    @Schema(name = "分享的文件列表")
    private List<RPanUserFileVO> userFileVOList;

    @Schema(name = "分享者的信息")
    private ShareUserInfoVO shareUserInfoVO;

}
