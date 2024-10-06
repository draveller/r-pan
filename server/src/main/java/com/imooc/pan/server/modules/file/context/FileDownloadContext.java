package com.imooc.pan.server.modules.file.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 下载文件的上下文实体对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadContext implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 响应对象
     */
    private HttpServletResponse response;

    /**
     * 用户ID
     */
    private Long userId;

}
