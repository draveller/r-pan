package com.imooc.pan.server.modules.file.context;

import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateFilenameContext implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long fileId;

    private String newFilename;

    private RPanUserFile entity;

}
