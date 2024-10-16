package com.imooc.pan.server.modules.file.converter;

import com.imooc.pan.core.constants.GlobalConst;
import com.imooc.pan.core.utils.EntityIdUtil;
import com.imooc.pan.server.common.utils.UserIdUtil;
import com.imooc.pan.server.modules.file.constants.FileConsts;
import com.imooc.pan.server.modules.file.context.*;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.server.modules.file.po.*;
import com.imooc.pan.server.modules.file.vo.FolderTreeNodeVO;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;
import com.imooc.pan.storage.engine.core.context.StoreFileChunkContext;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 用户模块实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface FileConverter {

    @Mapping(target = "parentId", expression = "java(com.imooc.pan.core.utils.EntityIdUtil.decrypt(po.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    CreateFolderContext convertPO2Context(CreateFolderPO po);

    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    @Mapping(target = "fileId", expression = "java(com.imooc.pan.core.utils.EntityIdUtil.decrypt(po.getFileId()))")
    UpdateFilenameContext convertPO2Context(UpdateFilenamePO po);

    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    DeleteFileContext convertPO2Context(DeleteFilePO deleteFilePO);

    @Mapping(target = "parentId", expression = "java(com.imooc.pan.core.utils.EntityIdUtil.decrypt(secUploadPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    SecUploadFileContext convertPO2Context(SecUploadPO secUploadPO);

    @Mapping(target = "parentId", expression = "java(com.imooc.pan.core.utils.EntityIdUtil.decrypt(fileUploadPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    FileUploadContext convertPO2Context(FileUploadPO fileUploadPO);

    @Mapping(target = "entity", ignore = true)
    FileSaveContext fileUploadContext2FileSaveContext(FileUploadContext context);

    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    FileChunkUploadContext convertPO2Context(FileChunkUploadPO fileChunkUploadPO);

    @Mapping(target = "mergeFlagEnum", ignore = true)
    FileChunkSaveContext fileChunkUploadContext2FileChunkSaveContext(FileChunkUploadContext context);

    @Mapping(target = "realPath", ignore = true)
    @Mapping(target = "inputStream", ignore = true)
    StoreFileChunkContext fileChunkSaveContext2StoreFileChunkContext(FileChunkSaveContext context);

    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    QueryUploadedChunksContext convertPO2Context(QueryUploadedChunksPO queryUploadedChunksPO);

    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    @Mapping(target = "entity", ignore = true)
    FileChunkMergeContext convertPO2Context(FileChunkMergePO fileChunkMergePO);

    FileChunkMergeAndSaveContext fileChunkMergeContext2FileChunkMergeAndSaveContext(FileChunkMergeContext context);

    @Mapping(target = "label", source = "rPanUserFile.filename")
    @Mapping(target = "children", expression = "java(new java.util.ArrayList())")
    FolderTreeNodeVO rPanUserFile2FolderTreeNodeVO(RPanUserFile rPanUserFile);

    default TransferFileContext convertPO2Context(TransferFilePO transferFilePO) {
        String fileIds = transferFilePO.getFileIds();
        String targetParentId = transferFilePO.getTargetParentId();

        List<Long> fileIdList = Arrays.stream(fileIds.split(GlobalConst.COMMON_SEPARATOR))
                .map(EntityIdUtil::decrypt).toList();
        Long decryptedTargetParentId = EntityIdUtil.decrypt(targetParentId);

        TransferFileContext context = new TransferFileContext();
        context.setFileIdList(fileIdList);
        context.setTargetParentId(decryptedTargetParentId);
        return context;
    }

    default CopyFileContext convertPO2Context(CopyFilePO copyFilePO) {
        String fileIds = copyFilePO.getFileIds();
        String targetParentId = copyFilePO.getTargetParentId();

        List<Long> fileIdList = Arrays.stream(fileIds.split(GlobalConst.COMMON_SEPARATOR)).map(EntityIdUtil::decrypt)
                .toList();
        Long decryptedTargetParentId = EntityIdUtil.decrypt(targetParentId);

        CopyFileContext context = new CopyFileContext();
        context.setFileIdList(fileIdList);
        context.setTargetParentId(decryptedTargetParentId);
        context.setUserId(UserIdUtil.get());
        return context;
    }

    default FileSearchContext convertPO2Context(FileSearchPO fileSearchPO) {
        FileSearchContext context = new FileSearchContext();
        context.setKeyword(fileSearchPO.getKeyword());

        String fileTypes = fileSearchPO.getFileTypes();
        if (StringUtils.isNotBlank(fileTypes) && !Objects.equals(FileConsts.ALL_FILE_TYPE, fileTypes)) {
            context.setFileTypeArray(Arrays.stream(fileTypes.split(GlobalConst.COMMON_SEPARATOR))
                    .map(Integer::parseInt).toList());
        }

        context.setUserId(UserIdUtil.get());
        return context;
    }

    RPanUserFileVO rPanUserFile2RPanUserFileVO(RPanUserFile userFile);

}
