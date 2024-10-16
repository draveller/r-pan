package com.imooc.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.utils.FileUtil;
import com.imooc.pan.core.utils.FileUtils;
import com.imooc.pan.server.common.event.log.PublishErrorLogEvent;
import com.imooc.pan.server.modules.file.context.FileChunkMergeAndSaveContext;
import com.imooc.pan.server.modules.file.context.FileSaveContext;
import com.imooc.pan.server.modules.file.entity.RPanFile;
import com.imooc.pan.server.modules.file.entity.RPanFileChunk;
import com.imooc.pan.server.modules.file.mapper.RPanFileMapper;
import com.imooc.pan.server.modules.file.service.IFileChunkService;
import com.imooc.pan.server.modules.file.service.IFileService;
import com.imooc.pan.storage.engine.core.StorageEngine;
import com.imooc.pan.storage.engine.core.context.DeleteFileContext;
import com.imooc.pan.storage.engine.core.context.MergeFileContext;
import com.imooc.pan.storage.engine.core.context.StoreFileContext;
import jakarta.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author 18063
 * @description 针对表【r_pan_file(物理文件信息表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:11:44
 */
@Service
public class FileServiceImpl extends ServiceImpl<RPanFileMapper, RPanFile>
        implements IFileService, ApplicationContextAware {

    @Resource
    private StorageEngine storageEngine;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private IFileChunkService iFileChunkService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 1. 上传单文件
     * 2. 保存实体记录
     *
     * @param context
     */
    @Override
    public void saveFile(FileSaveContext context) {
        this.storeMultipartFile(context);
        RPanFile entity = this.doSaveFile(context.getFilename(), context.getRealPath(), context.getTotalSize(),
                context.getIdentifier(), context.getUserId());
        context.setEntity(entity);
    }

    /**
     * 合并物理文件并保存物理文件记录
     * 1. 委托文件存储引擎合并文件分片
     * 2. 保存物理文件记录
     */
    @Override
    public void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context) {
        this.doMergeFileChunk(context);
        RPanFile entity = this.doSaveFile(context.getFilename(), context.getRealPath(), context.getTotalSize(),
                context.getIdentifier(), context.getUserId());
        context.setEntity(entity);
    }


    // ******************************** private ********************************

    /**
     * 委托文件存储引擎合并文件分片
     * 1. 查询文件分片的记录
     * 2. 根据文件分片的记录, 合并物理文件
     * 3. 删除文件分片记录
     * 4. 封装合并文件的真实存储路径到上下文信息中
     *
     * @param context
     */
    private void doMergeFileChunk(FileChunkMergeAndSaveContext context) {
        LambdaQueryWrapper<RPanFileChunk> wrapper = Wrappers.<RPanFileChunk>lambdaQuery()
                .eq(RPanFileChunk::getIdentifier, context.getIdentifier())
                .eq(RPanFileChunk::getCreateUser, context.getUserId())
                .gt(RPanFileChunk::getExpirationTime, new Date());
        List<RPanFileChunk> chunkRecordList = this.iFileChunkService.list(wrapper);

        if (CollectionUtils.isEmpty(chunkRecordList)) {
            throw new RPanBusinessException("文件分片记录不存在, 无法合并文件");
        }
        List<String> realPathList = chunkRecordList.stream()
                .sorted(Comparator.comparingInt(RPanFileChunk::getChunkNumber))
                .map(RPanFileChunk::getRealPath).toList();

        try {
            // 委托存储引擎去合并文件分片
            MergeFileContext mergeFileContext = new MergeFileContext();
            mergeFileContext.setFilename(context.getFilename());
            mergeFileContext.setIdentifier(context.getIdentifier());
            mergeFileContext.setUserId(context.getUserId());
            mergeFileContext.setRealPathList(realPathList);
            this.storageEngine.mergeFile(mergeFileContext);

            // 封装实体文件的真实存储路径
            context.setRealPath(mergeFileContext.getRealPath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPanBusinessException("文件分片合并失败");
        }

        // 删除文件分片记录
        List<Long> fileChunkRecordIdList = chunkRecordList.stream().map(RPanFileChunk::getId).toList();
        this.iFileChunkService.removeByIds(fileChunkRecordIdList);
    }

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
        RPanFile fileRecord = this.assembleRPanFile(filename, realPath, totalSize, identifier, userId);
        if (!this.save(fileRecord)) {
            try {
                DeleteFileContext deleteContext = new DeleteFileContext();
                deleteContext.setRealFilePathList(Lists.newArrayList(realPath));
                this.storageEngine.delete(deleteContext);
            } catch (Exception e) {
                e.printStackTrace();
                PublishErrorLogEvent publishErrorLogEvent = new PublishErrorLogEvent(
                        this, "文件物理删除失败, 请执行手动删除! 文件路径=" + realPath, userId);
                applicationContext.publishEvent(publishErrorLogEvent);
            }
        }
        return fileRecord;
    }

    /**
     * 拼装文件实体对象
     *
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     * @return
     */
    private RPanFile assembleRPanFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        RPanFile fileRecord = new RPanFile();
        fileRecord.setFilename(filename);
        fileRecord.setRealPath(realPath);
        fileRecord.setFileSize(String.valueOf(totalSize));
        fileRecord.setFileSizeDesc(FileUtils.byteCountToDisplaySize(totalSize));
        fileRecord.setFileSuffix(FileUtil.getFileSuffix(filename));
        fileRecord.setIdentifier(identifier);
        fileRecord.setCreateUser(userId);
        return fileRecord;
    }

    /**
     * 上传单文件
     * 该方法委托文件存储引擎实现
     */
    private void storeMultipartFile(FileSaveContext context) {
        try {
            StoreFileContext storeFileContext = new StoreFileContext();
            storeFileContext.setInputStream(context.getFile().getInputStream());
            storeFileContext.setFilename(context.getFilename());
            storeFileContext.setTotalSize(context.getTotalSize());

            this.storageEngine.store(storeFileContext);
            context.setRealPath(storeFileContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPanBusinessException("文件上传失败");
        }
    }

}




