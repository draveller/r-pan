package com.imooc.pan.server.modules.share.context;

import com.imooc.pan.server.modules.share.entity.RPanShare;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 创建分享链接上下文实体对象
 */
@Data
public class CreateShareUrlContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String shareName;

    private Integer shareType;

    /**
     * 分享类型（0 永久有效；1 7天有效；2 30天有效）
     */
    private Integer shareDayType;

    /**
     * 该分项对应的文件ID集合
     */
    private List<Long> shareFileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 已经保存的分享实体信息
     */
    private RPanShare entity;

}
