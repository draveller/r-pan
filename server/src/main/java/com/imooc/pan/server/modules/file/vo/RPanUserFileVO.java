package com.imooc.pan.server.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.web.serializer.Date2StringSerializer;
import com.imooc.pan.web.serializer.IdEncryptSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户查询文件列表响应实体
 */
@Schema(name ="文件列表响应实体")
@Data
public class RPanUserFileVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @Schema(name = "文件id")
    private Long fileId;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @Schema(name = "父文件夹id")
    private Long parentId;

    @Schema(name = "文件名")
    private String filename;

    @Schema(name = "文件大小描述")
    private String fileSizeDesc;

    @Schema(name = "文件类型, 1-文件夹; 0-文件")
    private Integer folderFlag;

    @Schema(name = "文件类型（1-普通文件 2-压缩文件 3-excel 4-word 5-pdf 6-txt 7-图片 8-音频 9-视频 10-ppt 11-源码文件 12-csv）")
    private Integer fileType;

    @Schema(name = "最近更新时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date updateTime;

}
