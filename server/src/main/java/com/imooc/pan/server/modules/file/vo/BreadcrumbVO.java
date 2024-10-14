package com.imooc.pan.server.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.web.serializer.IdEncryptSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@Schema(name = "面包屑列表展示实体")
public class BreadcrumbVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "文件id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @Schema(name = "父文件夹id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @Schema(name = "文件夹名称")
    private String name;

    /**
     * 实体转换
     */
    public static BreadcrumbVO transfer(RPanUserFile entity) {
        BreadcrumbVO vo = new BreadcrumbVO();
        if (Objects.nonNull(entity)) {
            vo.setId(entity.getId());
            vo.setParentId(entity.getParentId());
            vo.setName(entity.getFilename());
        }
        return vo;
    }

}
