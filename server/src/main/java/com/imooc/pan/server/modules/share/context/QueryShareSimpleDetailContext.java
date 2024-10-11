package com.imooc.pan.server.modules.share.context;

import com.imooc.pan.server.modules.share.entity.RPanShare;
import com.imooc.pan.server.modules.share.vo.ShareSimpleDetailVO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询分享简单详情上下文实体信息
 */
@Data
public class QueryShareSimpleDetailContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 分享对应的实体信息
     */
    private RPanShare entity;

    /**
     * 简单分享详情的VO对象
     */
    private ShareSimpleDetailVO vo;

}
