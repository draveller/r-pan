package com.imooc.pan.server.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.web.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户查询文件列表响应实体
 */
@ApiModel(value = "文件列表响应实体")
@Data
public class RPanUserFileVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @ApiModelProperty("文件id")
    private Long fileId;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @ApiModelProperty("父文件夹id")
    private Long parentId;

    @ApiModelProperty("文件名")
    private String filename;

    @ApiModelProperty("文件大小描述")
    private String fileSizeDesc;

    @ApiModelProperty("文件类型, 1-文件夹; 0-文件")
    private Integer folderFlag;

    @ApiModelProperty("文件类型（1-普通文件 2-压缩文件 3-excel 4-word 5-pdf 6-txt 7-图片 8-音频 9-视频 10-ppt 11-源码文件 12-csv）")
    private Integer fileType;

    @ApiModelProperty("最近更新时间")
    private Date updateTime;

}
