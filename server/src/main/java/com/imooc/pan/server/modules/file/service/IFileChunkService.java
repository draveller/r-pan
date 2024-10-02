package com.imooc.pan.server.modules.file.service;

import com.imooc.pan.server.modules.file.context.FileChunkSaveContext;
import com.imooc.pan.server.modules.file.entity.RPanFileChunk;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 18063
* @description 针对表【r_pan_file_chunk(文件分片信息表)】的数据库操作Service
* @createDate 2024-09-28 14:11:44
*/
public interface IFileChunkService extends IService<RPanFileChunk> {

    void saveChunkFile(FileChunkSaveContext fileChunkSaveContext);

}
