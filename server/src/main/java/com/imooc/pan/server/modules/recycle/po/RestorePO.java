package com.imooc.pan.server.modules.recycle.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@ApiModel("还原已删除文件参数实体")
public class RestorePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "要还原的文件id集合, 使用分隔符分隔", required = true)
    @NotBlank(message = "要还原的文件id集合不能为空")
    private String fileIds;

}
