package com.imooc.pan.server.modules.share.context;

import com.imooc.pan.server.modules.share.entity.RPanShare;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询下一级文件列表的上下文实体信息
 */
@Data
public class QueryChildFileListContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 父文件夹的ID
     */
    private Long parentId;

    /**
     * 分享对应的实体信息
     */
    private RPanShare entity;

}
