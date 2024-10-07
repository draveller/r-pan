package com.imooc.pan.server.modules.file.context;

import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 文件转移操作上下文实体对象
 */
@Data
public class TransferFileContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 要转移的文件id集合
     */
    private List<Long> fileIdList;

    /**
     * 目标文件夹id
     */
    private Long targetParentId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 要转移的文件列表
     */
    private List<RPanUserFile> prepareRecords;

}
