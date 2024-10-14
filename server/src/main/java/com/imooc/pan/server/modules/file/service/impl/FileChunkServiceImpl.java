package com.imooc.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.core.constants.MsgConst;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.server.common.config.PanServerProps;
import com.imooc.pan.server.modules.file.context.FileChunkSaveContext;
import com.imooc.pan.server.modules.file.converter.FileConverter;
import com.imooc.pan.server.modules.file.entity.RPanFileChunk;
import com.imooc.pan.server.modules.file.enums.MergeFlagEnum;
import com.imooc.pan.server.modules.file.mapper.RPanFileChunkMapper;
import com.imooc.pan.server.modules.file.service.IFileChunkService;
import com.imooc.pan.storage.engine.core.StorageEngine;
import com.imooc.pan.storage.engine.core.context.StoreFileChunkContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author 18063
 * @description 针对表【r_pan_file_chunk(文件分片信息表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:11:44
 */
@Service
public class FileChunkServiceImpl extends ServiceImpl<RPanFileChunkMapper, RPanFileChunk>
        implements IFileChunkService {

    @Resource
    private PanServerProps panServerProps;

    @Resource
    private FileConverter fileConverter;

    @Resource
    private StorageEngine storageEngine;

    /**
     * 文件分片保存
     * 1. 保存文件分片和记录
     * 2. 判断文件分片是否全部上传完成
     */
    @Override
    public synchronized void saveChunkFile(FileChunkSaveContext context) {
        this.doSaveChunkFile(context);
        this.doJudgeMergeFile(context);
    }


    // ******************************** private ********************************

    /**
     * 判断是否所有的文件均上传完成
     */
    private void doJudgeMergeFile(FileChunkSaveContext context) {
        LambdaQueryWrapper<RPanFileChunk> wrapper = Wrappers.<RPanFileChunk>lambdaQuery()
                .eq(RPanFileChunk::getIdentifier, context.getIdentifier())
                .eq(RPanFileChunk::getCreateUser, context.getUserId());
        long count = this.count(wrapper);
        if (count == Long.valueOf(context.getTotalChunks())) {
            context.setMergeFlagEnum(MergeFlagEnum.READY);
        }
    }

    /**
     * 执行文件分片上传保存的操作
     * 1. 委托文件存储引擎存储文件分片
     * 2. 保存文件分片记录
     */
    private void doSaveChunkFile(FileChunkSaveContext context) {
        this.doStoreFileChunk(context);
        this.doSaveRecord(context);
    }

    /**
     * 保存文件分片记录
     */
    private void doSaveRecord(FileChunkSaveContext context) {
        RPanFileChunk fileChunk = new RPanFileChunk();
        fileChunk.setId(IdUtil.get());
        fileChunk.setIdentifier(context.getIdentifier());
        fileChunk.setRealPath(context.getRealPath());
        fileChunk.setChunkNumber(context.getChunkNumber());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusDays(this.panServerProps.getChunkFileExpirationDays());
        fileChunk.setExpirationTime(expiration);
        fileChunk.setCreateUser(context.getUserId());
        if (!this.save(fileChunk)) {
            throw new RPanBusinessException(MsgConst.FILE_CHUNK_UPLOAD_FAILED);
        }
    }

    private void doStoreFileChunk(FileChunkSaveContext context) {
        try {
            StoreFileChunkContext storeFileChunkContext = fileConverter.fileChunkSaveContext2StoreFileChunkContext(context);
            storeFileChunkContext.setInputStream(context.getFile().getInputStream());
            storageEngine.storeChunk(storeFileChunkContext);
            context.setRealPath(storeFileChunkContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPanBusinessException(MsgConst.FILE_CHUNK_UPLOAD_FAILED);
        }
    }

}
