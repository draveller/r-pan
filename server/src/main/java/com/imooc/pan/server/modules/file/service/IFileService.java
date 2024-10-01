package com.imooc.pan.server.modules.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pan.server.modules.file.context.FileSaveContext;
import com.imooc.pan.server.modules.file.entity.RPanFile;

/**
 * @author 18063
 * @description 针对表【r_pan_file(物理文件信息表)】的数据库操作Service
 * @createDate 2024-09-28 14:11:44
 */
public interface IFileService extends IService<RPanFile> {

    /**
     * 上传单文件并保存实体记录
     *
     * @param context
     */
    void saveFile(FileSaveContext context);

}
