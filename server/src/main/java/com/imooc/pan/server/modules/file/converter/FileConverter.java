package com.imooc.pan.server.modules.file.converter;

import com.imooc.pan.server.modules.file.context.*;
import com.imooc.pan.server.modules.file.po.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户模块实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface FileConverter {

    @Mapping(target = "parentId", expression = "java(com.imooc.pan.core.utils.IdUtil.decrypt(createFolderPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    CreateFolderContext convertPO2Context(CreateFolderPO createFolderPO);

    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    UpdateFilenameContext convertPO2Context(UpdateFilenamePO updateFilenamePO);

    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    DeleteFileContext convertPO2Context(DeleteFilePO deleteFilePO);

    @Mapping(target = "parentId", expression = "java(com.imooc.pan.core.utils.IdUtil.decrypt(secUploadPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    SecUploadContext convertPO2Context(SecUploadPO secUploadPO);

    @Mapping(target = "parentId", expression = "java(com.imooc.pan.core.utils.IdUtil.decrypt(fileUploadPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    FileUploadContext convertPO2Context(FileUploadPO fileUploadPO);

    @Mapping(target = "record", ignore = true)
    FileSaveContext fileUploadContext2FileSaveContext(FileUploadContext context);

}
