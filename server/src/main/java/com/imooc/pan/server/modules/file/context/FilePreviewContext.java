package com.imooc.pan.server.modules.file.context;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 预览文件的上下文实体对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilePreviewContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 响应对象
     */
    private transient HttpServletResponse response;

    /**
     * 用户ID
     */
    private Long userId;

}
