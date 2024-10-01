package com.imooc.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeleteFileContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 要删除的文件id集合
     */
    private List<Long> fileIdList;

    /**
     * 当前的登录用户id
     */
    private Long userId;

}
