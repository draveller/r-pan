package com.imooc.pan.server.modules.file.converter;

import com.imooc.pan.server.modules.file.context.CreateFolderContext;
import com.imooc.pan.server.modules.file.context.UpdateFilenameContext;
import com.imooc.pan.server.modules.file.po.CreateFolderPO;
import com.imooc.pan.server.modules.file.po.UpdateFilenamePO;
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

    @Mapping(target = "fileId", expression = "java(com.imooc.pan.core.utils.IdUtil.decrypt(updateFilenamePO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.imooc.pan.server.common.utils.UserIdUtil.get())")
    UpdateFilenameContext convertPO2Context(UpdateFilenamePO updateFilenamePO);

}
