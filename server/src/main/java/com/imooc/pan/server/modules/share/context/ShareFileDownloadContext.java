package com.imooc.pan.server.modules.share.context;

import lombok.Data;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分享文件下载上下文实体对象
 */
@Data
public class ShareFileDownloadContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 要下载的文件ID
     */
    private Long fileId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 相应实体
     */
    private transient HttpServletResponse response;

}
