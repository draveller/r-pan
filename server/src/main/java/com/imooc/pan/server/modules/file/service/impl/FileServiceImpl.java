package com.imooc.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.core.utils.FileUtil;
import com.imooc.pan.core.utils.FileUtils;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.server.modules.file.context.FileSaveContext;
import com.imooc.pan.server.modules.file.entity.RPanFile;
import com.imooc.pan.server.modules.file.mapper.RPanFileMapper;
import com.imooc.pan.server.modules.file.service.IFileService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 18063
 * @description 针对表【r_pan_file(物理文件信息表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:11:44
 */
@Service
public class FileServiceImpl extends ServiceImpl<RPanFileMapper, RPanFile>
        implements IFileService {

    /**
     * 1. 上传单文件
     * 2. 保存实体记录
     *
     * @param context
     */
    @Override
    public void saveFile(FileSaveContext context) {
        this.storeMultipartFile(context);
        RPanFile record = this.doSaveFile(context.getFilename(), context.getRealPath(), context.getTotalSize(),
                context.getIdentifier(), context.getUserId());
        context.setRecord(record);
    }


    // ******************************** private ********************************

    /**
     * 保存实体文件记录
     *
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     */
    private RPanFile doSaveFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        RPanFile record = this.assembleRPanFile(filename, realPath, totalSize, identifier, userId);
        if (!this.save(record)) {
            // todo: 删除已上传的物理文件
        }
        return record;
    }

    /**
     * 拼装文件实体对象
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     * @return
     */
    private RPanFile assembleRPanFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        RPanFile record = new RPanFile();
        record.setFileId(IdUtil.get());
        record.setFilename(filename);
        record.setRealPath(realPath);
        record.setFileSize(String.valueOf(totalSize));
        record.setFileSizeDesc(FileUtils.byteCountToDisplaySize(totalSize));
        record.setFileSuffix(FileUtil.getFileSuffix(filename));
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        return record;
    }

    /**
     * 上传单文件
     * 该方法委托文件存储引擎实现
     *
     * @param context
     */
    private void storeMultipartFile(FileSaveContext context) {


    }

}




