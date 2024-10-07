package com.imooc.pan.server.common.listener.file;

import com.alibaba.fastjson.JSON;
import com.imooc.pan.server.common.event.file.FilePhysicalDeleteEvent;
import com.imooc.pan.server.common.event.log.ErrorLogEvent;
import com.imooc.pan.server.modules.file.entity.RPanFile;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.server.modules.file.enums.FolderFlagEnum;
import com.imooc.pan.server.modules.file.service.IFileService;
import com.imooc.pan.server.modules.file.service.IUserFileService;
import com.imooc.pan.storage.engine.core.StorageEngine;
import com.imooc.pan.storage.engine.core.context.DeleteFileContext;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文件物理删除监听器
 */
@Component
public class FilePhysicalDeleteEventListener implements ApplicationContextAware {

    @Resource
    private IFileService iFileService;

    @Resource
    private IUserFileService iUserFileService;

    @Resource
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 监听文件物理删除事件执行器
     * <p>
     * 该执行器是一个资源释放器，释放被物理删除的文件列表中关联的实体文件记录
     * <p>
     * 1、查询所有无引用的实体文件记录
     * 2、删除记录
     * 3、物理清理文件（委托文件存储引擎）
     *
     * @param event
     */
    @EventListener(classes = FilePhysicalDeleteEvent.class)
    @Async(value = "eventListenerTaskExecutor")
    public void physicalDeleteFile(FilePhysicalDeleteEvent event) {
        List<RPanUserFile> allRecords = event.getAllRecords();
        if (CollectionUtils.isEmpty(allRecords)) {
            return;
        }
        List<Long> realFileIdList = findAllUnusedRealFileIdList(allRecords);
        List<RPanFile> realFileRecords = iFileService.listByIds(realFileIdList);
        if (CollectionUtils.isEmpty(realFileRecords)) {
            return;
        }
        if (!iFileService.removeByIds(realFileIdList)) {
            applicationContext.publishEvent(new ErrorLogEvent(this, "实体文件记录："
                    + JSON.toJSONString(realFileIdList) + "， 物理删除失败，请执行手动删除", 0L));
            return;
        }
        physicalDeleteFileByStorageEngine(realFileRecords);
    }

    // -------------------------------- private --------------------------------

    /**
     * 委托文件存储引擎执行物理文件的删除
     */
    private void physicalDeleteFileByStorageEngine(List<RPanFile> realFileRecords) {
        List<String> realPathList = realFileRecords.stream().map(RPanFile::getRealPath).collect(Collectors.toList());
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setRealFilePathList(realPathList);
        try {
            storageEngine.delete(deleteFileContext);
        } catch (IOException e) {
            applicationContext.publishEvent(new ErrorLogEvent(this, "实体文件："
                    + JSON.toJSONString(realPathList) + "， 物理删除失败，请执行手动删除", 0L));
        }
    }

    /**
     * 查找所有没有被引用的真实文件记录ID集合
     */
    private List<Long> findAllUnusedRealFileIdList(List<RPanUserFile> allRecords) {
        return allRecords.stream()
                .filter(ele -> Objects.equals(ele.getFolderFlag(), FolderFlagEnum.NO.getCode()))
                .filter(this::isUnused)
                .map(RPanUserFile::getRealFileId)
                .collect(Collectors.toList());
    }

    /**
     * 校验文件的真实文件ID是不是没有被引用了
     */
    private boolean isUnused(RPanUserFile userFile) {
        Long count = iUserFileService.lambdaQuery()
                .eq(RPanUserFile::getRealFileId, userFile.getRealFileId())
                .count();
        return count.equals(0L);
    }


}
