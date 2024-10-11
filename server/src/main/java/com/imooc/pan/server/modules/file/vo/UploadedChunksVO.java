package com.imooc.pan.server.modules.file.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "查询用户已上传的文件分片列表返回实体")
public class UploadedChunksVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "已上传的文件分片的序号列表")
    private List<Integer> uploadedChunks;

}
