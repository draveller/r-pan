package com.imooc.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.core.constants.RPanConstants;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.utils.FileUtil;
import com.imooc.pan.server.common.event.file.DeleteFileEvent;
import com.imooc.pan.server.modules.file.constants.DelFlagEnum;
import com.imooc.pan.server.modules.file.constants.FileConstants;
import com.imooc.pan.server.modules.file.context.*;
import com.imooc.pan.server.modules.file.converter.FileConverter;
import com.imooc.pan.server.modules.file.entity.RPanFile;
import com.imooc.pan.server.modules.file.entity.RPanFileChunk;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.server.modules.file.enums.FileTypeEnum;
import com.imooc.pan.server.modules.file.enums.FolderFlagEnum;
import com.imooc.pan.server.modules.file.mapper.RPanUserFileMapper;
import com.imooc.pan.server.modules.file.service.IFileChunkService;
import com.imooc.pan.server.modules.file.service.IFileService;
import com.imooc.pan.server.modules.file.service.IUserFileService;
import com.imooc.pan.server.modules.file.vo.FileChunkUploadVO;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;
import com.imooc.pan.server.modules.file.vo.UploadedChunksVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 18063
 * @description 针对表【r_pan_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:11:44
 */
@Service
public class UserFileServiceImpl extends ServiceImpl<RPanUserFileMapper, RPanUserFile>
        implements IUserFileService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private FileConverter fileConverter;

    @Autowired
    private IFileChunkService iFileChunkService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 创建文件夹的业务方法实现
     *
     * @param createFolderContext 上下文字段集合对象
     * @return
     */
    @Override
    public Long createFolder(CreateFolderContext createFolderContext) {
        return this.saveUserFile(createFolderContext.getParentId(), createFolderContext.getFolderName(),
                FolderFlagEnum.YES, null, null, createFolderContext.getUserId(), null
        );
    }

    /**
     * 按照用户id获取根文件夹信息
     *
     * @param userId 用户id
     * @return
     */
    @Override
    public RPanUserFile getUserRootFile(Long userId) {
        return this.getOne(
                Wrappers.<RPanUserFile>lambdaQuery()
                        .eq(RPanUserFile::getUserId, userId)
                        .eq(RPanUserFile::getParentId, FileConstants.TOP_PARENT_ID)
                        .eq(RPanUserFile::getDelFlag, DelFlagEnum.NO.getCode())
                        .eq(RPanUserFile::getFolderFlag, FolderFlagEnum.YES.getCode())
        );
    }

    /**
     * 查询用户的文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<RPanUserFileVO> getFileList(QueryFileListContext context) {
        return this.baseMapper.selectFileList(context);
    }

    /**
     * 更新文件名称
     *
     * @param context
     */
    @Override
    public void updateFilename(UpdateFilenameContext context) {
        this.checkUpdateFilenameCondition(context);
        this.doUpdateFilename(context);
    }

    /**
     * 批量删除用户文件
     * 1. 校验删除的条件是否符合
     * 2. 执行批量删除的动作
     * 3. 发布批量删除文件的事件, 给其他模块订阅使用
     *
     * @param context
     */
    @Override
    public void deleteFile(DeleteFileContext context) {
        this.checkFileDeleteCondition(context);
        this.doDeleteFile(context);
        this.afterFileDelete(context);
    }

    /**
     * 文件秒传
     * 1. 通过文件的唯一标识查找对应的实体文件记录
     * 2. 如果没有查到直接返回失败
     * 3. 如果查到记录, 直接挂载关联关系, 返回成功
     *
     * @param context
     * @return
     */
    @Override
    public boolean secUpload(SecUploadContext context) {
        RPanFile record = this.getFileByUserIdAndIdentifier(context);

        if (record == null) {
            return false;
        }
        this.saveUserFile(context.getParentId(), context.getFilename(), FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())),
                record.getFileId(), context.getUserId(), record.getFileSizeDesc());
        return true;
    }

    /**
     * 单文件上传
     * 1. 上传文件并保存实体文件记录
     * 2. 保存用户据文件的关系记录
     *
     * @param context
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upload(FileUploadContext context) {
        this.saveFile(context);
        this.saveUserFile(context.getParentId(), context.getFilename(), FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())),
                context.getRecord().getFileId(), context.getUserId(), context.getRecord().getFileSizeDesc());
    }

    /**
     * 文件分片上传
     * 1. 上传实体文件
     * 2. 保存分片文件的记录
     * 3. 校验是否所有的分片都上传完成
     *
     * @param context
     * @return
     */
    @Override
    public FileChunkUploadVO chunkUpload(FileChunkUploadContext context) {
        FileChunkSaveContext fileChunkSaveContext = this.fileConverter.fileChunkUploadContext2FileChunkSaveContext(context);
        this.iFileChunkService.saveChunkFile(fileChunkSaveContext);
        FileChunkUploadVO vo = new FileChunkUploadVO();
        vo.setMergeFlag(fileChunkSaveContext.getMergeFlagEnum().getCode());
        return vo;
    }

    /**
     * 查询用户已上传的分片列表
     * 1. 查询已上传的分片列表
     * 2. 封装返回实体
     *
     * @param context
     * @return
     */
    @Override
    public UploadedChunksVO getUploadedChunks(QueryUploadedChunksContext context) {
        LambdaQueryWrapper<RPanFileChunk> wrapper = Wrappers.<RPanFileChunk>lambdaQuery()
                .select(RPanFileChunk::getChunkNumber)
                .eq(RPanFileChunk::getIdentifier, context.getIdentifier())
                .eq(RPanFileChunk::getCreateUser, context.getUserId())
                .gt(RPanFileChunk::getExpirationTime, new Date());

        List<Integer> uploadedChunks = this.iFileChunkService.listObjs(wrapper, val -> (Integer) val);
        UploadedChunksVO vo = new UploadedChunksVO();
        vo.setUploadedChunks(uploadedChunks);
        return vo;
    }

    // ******************************** private ********************************

    /**
     * 上传文件并保存实体文件记录
     * 委托给实体文件的service去完成此操作
     *
     * @param context
     */
    private void saveFile(FileUploadContext context) {
        FileSaveContext fileSaveContext = this.fileConverter.fileUploadContext2FileSaveContext(context);
        this.iFileService.saveFile(fileSaveContext);
        context.setRecord(fileSaveContext.getRecord());
    }

    /**
     * 通过用户id和唯一标识来查找文件
     *
     * @param context
     * @return
     */
    private RPanFile getFileByUserIdAndIdentifier(SecUploadContext context) {

        LambdaQueryWrapper<RPanFile> wrapper = Wrappers.<RPanFile>lambdaQuery()
                .eq(RPanFile::getCreateUser, context.getUserId())
                .eq(RPanFile::getIdentifier, context.getIdentifier());
        List<RPanFile> records = this.iFileService.list(wrapper);

        if (CollectionUtils.isEmpty(records)) {
            return null;
        }

        return records.get(0);
    }

    /**
     * 文件删除的后置操作
     * 1. 对外发布文件删除的事件
     *
     * @param context
     */
    private void afterFileDelete(DeleteFileContext context) {
        DeleteFileEvent deleteFileEvent = new DeleteFileEvent(this, context.getFileIdList());
        this.applicationContext.publishEvent(deleteFileEvent);
    }

    /**
     * 执行文件删除操作
     *
     * @param context
     */
    private void doDeleteFile(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();

        LambdaUpdateWrapper<RPanUserFile> wrapper = Wrappers.<RPanUserFile>lambdaUpdate()
                .in(RPanUserFile::getFileId, fileIdList)
                .set(RPanUserFile::getDelFlag, DelFlagEnum.YES.getCode())
                .set(RPanUserFile::getUpdateUser, context.getUserId())
                .set(RPanUserFile::getUpdateTime, new Date());

        if (!this.update(wrapper)) {
            throw new RuntimeException("文件(夹)删除失败");
        }
    }

    /**
     * 删除文件之前的前置校验
     * 1. 文件id合法性校验
     * 2. 用户拥有删除该文件的权限
     *
     * @param context
     */
    private void checkFileDeleteCondition(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        List<RPanUserFile> rPanUserFiles = this.listByIds(fileIdList);
        if (rPanUserFiles.size() != fileIdList.size()) {
            throw new RPanBusinessException("存在不合法的文件(夹)记录");
        }

        Set<Long> fileIdSet = rPanUserFiles.stream().map(RPanUserFile::getFileId).collect(Collectors.toSet());
        int oldSize = fileIdSet.size();
        fileIdSet.addAll(fileIdList);
        int newSize = fileIdSet.size();
        if (oldSize != newSize) {
            throw new RPanBusinessException("存在不合法的文件(夹)记录");
        }

        boolean allMatch = rPanUserFiles.stream()
                .allMatch(rPanUserFile -> Objects.equals(rPanUserFile.getUserId(), context.getUserId()));
        if (!allMatch) {
            throw new RPanBusinessException("存在不合法的文件(夹)记录");
        }

    }

    /**
     * 执行文件重命名的操作
     *
     * @param context
     */
    private void doUpdateFilename(UpdateFilenameContext context) {
        RPanUserFile entity = context.getEntity();
        entity.setFilename(context.getNewFilename());
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(context.getUserId());

        if (!this.updateById(entity)) {
            throw new RuntimeException("文件(夹)重命名失败");
        }
    }

    /**
     * 更新文件名称的条件校验
     * 1. 文件id是有效的
     * 2. 用户有权限更新该文件的名称
     * 3. 新旧文件名不能一样
     * 4. 不能使用当前文件夹下面的子文件名称
     *
     * @param context
     */
    private void checkUpdateFilenameCondition(UpdateFilenameContext context) {
        Long fileId = context.getFileId();
        RPanUserFile entity = Optional.ofNullable(this.getById(fileId))
                .orElseThrow(() -> new RPanBusinessException("文件(夹)不存在"));

        if (!Objects.equals(entity.getUserId(), context.getUserId())) {
            throw new RPanBusinessException("当前用户没有权限更新该文件(夹)的名称");
        }

        if (Objects.equals(entity.getFilename(), context.getNewFilename())) {
            throw new RPanBusinessException("新文件名称不能与旧文件一致");
        }

        LambdaQueryWrapper<RPanUserFile> wrapper = Wrappers.<RPanUserFile>lambdaQuery()
                .eq(RPanUserFile::getParentId, entity.getParentId())
                .eq(RPanUserFile::getFilename, context.getNewFilename());
        if (this.count(wrapper) > 0) {
            throw new RPanBusinessException("当前文件夹下已有同名文件");
        }
        context.setEntity(entity);
    }

    /**
     * 保存用户文件(夹)的映射记录信息, 使用字段如下
     *
     * @param parentId       父级目录id
     * @param filename       文件(夹)名称
     * @param folderFlagEnum 文件夹标识, 以此区分'文件夹'和'文件'
     * @param fileType       文件类型
     * @param realFileId     真实文件id
     * @param userId         用户id, 即所属人id
     * @param fileSizeDesc   文件大小描述
     * @return 返回的文件(夹)id
     */
    private Long saveUserFile(Long parentId, String filename, FolderFlagEnum folderFlagEnum,
                              Integer fileType, Long realFileId, Long userId, String fileSizeDesc) {

        RPanUserFile entity = assembleRPanUserFile(parentId, filename, folderFlagEnum, fileType, realFileId, userId, fileSizeDesc);
        if (!this.save(entity)) {
            throw new RuntimeException("保存文件信息失败");
        }
        return entity.getFileId();
    }

    /**
     * 将属性集成为实体类, 并对该对象进行文件(夹)名称的处理
     * <p>
     * 1. 将将属性集成为文件(夹)实体类对象
     * 2. 处理文件命名一致的问题
     *
     * @return 集成后的实体类对象, 并且已自动进行了重命名
     */
    private RPanUserFile assembleRPanUserFile(Long parentId, String filename, FolderFlagEnum folderFlagEnum,
                                              Integer fileType, Long realFileId, Long userId, String fileSizeDesc) {
        RPanUserFile entity = new RPanUserFile();
        entity.setParentId(parentId);
        entity.setFilename(filename);
        entity.setFolderFlag(folderFlagEnum.getCode());
        entity.setFileType(fileType);
        entity.setRealFileId(realFileId);
        entity.setUserId(userId);
        entity.setFileSizeDesc(fileSizeDesc);
        entity.setDelFlag(DelFlagEnum.NO.getCode());
        entity.setCreateUser(userId);
        entity.setCreateTime(new Date());
        entity.setUpdateUser(userId);
        entity.setUpdateTime(new Date());

        this.handleDuplicateFilename(entity);

        return entity;
    }

    /**
     * 处理重复的文件名
     * 如果同一文件夹下存在相同的文件名，则自动在文件名后加上数字后缀
     *
     * @param entity 文件(夹)对象
     */
    private void handleDuplicateFilename(RPanUserFile entity) {
        String filename = entity.getFilename();
        String newFilenameWithoutSuffix;
        String newFilenameSuffix;

        int newFilenamePointPosition = filename.lastIndexOf(RPanConstants.POINT_STR);

        if (newFilenamePointPosition == RPanConstants.MINUS_ONE_INT) {
            newFilenameWithoutSuffix = filename;
            newFilenameSuffix = StringUtils.EMPTY;
        } else {
            newFilenameWithoutSuffix = filename.substring(RPanConstants.ZERO_INT, newFilenamePointPosition);
            newFilenameSuffix = filename.replace(newFilenameWithoutSuffix, StringUtils.EMPTY);
        }

        int count = getDuplicateFilename(entity, newFilenameWithoutSuffix);
        if (count == 0) {
            return;
        }

        String newFilename = assembleNewFilename(newFilenameWithoutSuffix, count, newFilenameSuffix);

        entity.setFilename(newFilename);
    }

    /**
     * 拼装新文件名称
     * 拼装规则参考操作系统重命名规则
     *
     * @param newFilenameWithoutSuffix
     * @param count
     * @param newFilenameSuffix
     * @return
     */
    private String assembleNewFilename(String newFilenameWithoutSuffix, int count, String newFilenameSuffix) {
        return new StringBuilder(newFilenameWithoutSuffix)
                .append(FileConstants.CN_LEFT_PARENTHESIS_STR)
                .append(count)
                .append(FileConstants.CN_RIGHT_PARENTHESIS_STR)
                .append(newFilenameSuffix)
                .toString();
    }

    /**
     * 查找同一父文件夹下面的同名文件数量
     *
     * @param entity
     * @param newFilenameWithoutSuffix
     * @return
     */
    private int getDuplicateFilename(RPanUserFile entity, String newFilenameWithoutSuffix) {

        LambdaQueryWrapper<RPanUserFile> wrapper = Wrappers.<RPanUserFile>lambdaQuery()
                .eq(RPanUserFile::getParentId, entity.getParentId())
                .eq(RPanUserFile::getFolderFlag, entity.getFolderFlag())
                .eq(RPanUserFile::getDelFlag, DelFlagEnum.NO)
                .likeLeft(RPanUserFile::getFilename, newFilenameWithoutSuffix);

        return this.count(wrapper);
    }

}




