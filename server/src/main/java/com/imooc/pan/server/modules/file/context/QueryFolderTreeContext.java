package com.imooc.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;
@Data
public class QueryFolderTreeContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前登录的用户id
     */
    private Long userId;

}
