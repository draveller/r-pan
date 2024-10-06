package com.imooc.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.imooc.pan.core.constants.RPanConstants;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.utils.FileUtil;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.server.common.event.file.DeleteFileEvent;
import com.imooc.pan.server.common.event.search.UserSearchEvent;
import com.imooc.pan.server.common.utils.HttpUtil;
import com.imooc.pan.server.modules.file.constants.DelFlagEnum;
import com.imooc.pan.server.modules.file.constants.FileConsts;
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
import com.imooc.pan.server.modules.file.vo.*;
import com.imooc.pan.storage.engine.core.StorageEngine;
import com.imooc.pan.storage.engine.core.context.ReadFileContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 18063
 * @description 针对表【r_pan_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:11:44
 */
@Slf4j
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

    @Autowired
    private StorageEngine storageEngine;

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
                        .eq(RPanUserFile::getParentId, FileConsts.TOP_PARENT_ID)
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
    public boolean secUpload(SecUploadFileContext context) {
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

    /**
     * 文件分片合并
     * 1. 文件分片物理合并
     * 2. 保存文件实体记录
     * 3. 保存文件用户关系映射
     *
     * @param context
     */
    @Override
    public void mergeFile(FileChunkMergeContext context) {
        this.mergeFileChunkAndSaveFile(context);
        this.saveUserFile(context.getParentId(), context.getFilename(), FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())),
                context.getRecord().getFileId(), context.getUserId(), context.getRecord().getFileSizeDesc());
    }

    /**
     * 文件下载
     * 1. 参数校验: 校验文件是否存在, 文件是否属于当前用户,
     * 2. 判断该文件是否是文件夹, 文件夹不支持下载
     * 3. 执行下载动作
     *
     * @param context
     */
    @Override
    public void download(FileDownloadContext context) {
        RPanUserFile record = this.getById(context.getFileId());
        this.checkOperatePermission(record, context.getUserId());
        if (this.checkIsFolder(record)) {
            throw new RPanBusinessException("文件夹暂不支持下载");
        }
        this.doDownLoad(record, context.getResponse());
    }

    /**
     * 预览文件
     * 1. 参数校验: 校验文件是否存在, 文件是否属于当前用户,
     * 2. 判断该文件是否是文件夹, 文件夹不支持预览
     * 3. 执行预览动作
     *
     * @param context
     */
    @Override
    public void preview(FilePreviewContext context) {
        RPanUserFile record = this.getById(context.getFileId());
        this.checkOperatePermission(record, context.getUserId());
        if (this.checkIsFolder(record)) {
            throw new RPanBusinessException("文件夹暂不支持下载");
        }
        this.doPreview(record, context.getResponse());
    }

    /**
     * 查询用户的文件夹树
     * 1. 查询出该用户的所有文件夹列表
     * 2. 在内存中拼装文件夹树
     *
     * @param context
     * @return
     */
    @Override
    public List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext context) {
        List<RPanUserFile> folderRecords = this.queryFolderRecords(context.getUserId());
        return this.assembleFolderTreeNodeVOList(folderRecords);
    }

    /**
     * 文件转移
     * 1. 权限校验
     * 2. 执行动作
     *
     * @param context
     */
    @Override
    public void transfer(TransferFileContext context) {
        this.checkTransferCondition(context);
        this.doTransfer(context);
    }

    /**
     * 文件复制
     * 1. 条件校验
     * 2. 执行动作
     *
     * @param context
     */
    @Override
    public void copy(CopyFileContext context) {
        this.checkCopyCondition(context);
        this.doCopy(context);
    }

    /**
     * 文件列表搜索
     * 1. 执行文件搜索
     * 2. 拼装文件的父文件夹名称
     * 3. 执行文件搜索后的后置动作
     *
     * @param context
     * @return
     */
    @Override
    public List<FileSearchResultVO> search(FileSearchContext context) {
        List<FileSearchResultVO> result = this.doSearch(context);
        this.fillParentFilename(result);
        this.afterSearch(context);
        return result;
    }

    /**
     * 获取面包屑列表
     * <p>
     * 1、获取用户所有文件夹信息
     * 2、拼接需要用到的面包屑的列表
     *
     * @param context
     * @return
     */
    @Override
    public List<BreadcrumbVO> getBreadcrumbs(QueryBreadcrumbsContext context) {
        List<RPanUserFile> folderRecords = queryFolderRecords(context.getUserId());
        Map<Long, BreadcrumbVO> prepareBreadcrumbVOMap = folderRecords.stream()
                .map(BreadcrumbVO::transfer).collect(Collectors.toMap(BreadcrumbVO::getId, a -> a));
        BreadcrumbVO currentNode;
        Long fileId = context.getFileId();
        List<BreadcrumbVO> result = Lists.newLinkedList();
        do {
            currentNode = prepareBreadcrumbVOMap.get(fileId);
            if (Objects.nonNull(currentNode)) {
                result.add(0, currentNode);
                fileId = currentNode.getParentId();
            }
        } while (Objects.nonNull(currentNode));
        return result;
    }

    /**
     * 递归查询所有的子文件信息
     *
     * @param records
     * @return
     */
    @Override
    public List<RPanUserFile> findAllFileRecords(List<RPanUserFile> records) {
        List<RPanUserFile> result = Lists.newArrayList(records);
        if (CollectionUtils.isEmpty(result)) {
            return result;
        }
        long folderCount = result.stream().filter(record -> Objects.equals(record.getFolderFlag(), FolderFlagEnum.YES.getCode())).count();
        if (folderCount == 0) {
            return result;
        }
        records.stream().forEach(record -> doFindAllChildRecords(result, record));
        return result;
    }

    /**
     * 文件下载 不校验用户是否是否是上传用户
     *
     * @param context
     */
    @Override
    public void downloadWithoutCheckUser(FileDownloadContext context) {
        RPanUserFile record = getById(context.getFileId());
        if (Objects.isNull(record)) {
            throw new RPanBusinessException("当前文件记录不存在");
        }
        if (checkIsFolder(record)) {
            throw new RPanBusinessException("文件夹暂不支持下载");
        }
        doDownload(record, context.getResponse());
    }

    /**
     * 递归查询所有的子文件信息
     *
     * @param fileIdList
     * @return
     */
    @Override
    public List<RPanUserFile> findAllFileRecordsByFileIdList(List<Long> fileIdList) {
        if (CollectionUtils.isEmpty(fileIdList)) {
            return Lists.newArrayList();
        }
        List<RPanUserFile> records = listByIds(fileIdList);
        if (CollectionUtils.isEmpty(records)) {
            return Lists.newArrayList();
        }
        return findAllFileRecords(records);
    }

    /**
     * 实体转换
     *
     * @param records
     * @return
     */
    @Override
    public List<RPanUserFileVO> transferVOList(List<RPanUserFile> records) {
        if (CollectionUtils.isEmpty(records)) {
            return Lists.newArrayList();
        }
        return records.stream().map(fileConverter::rPanUserFile2RPanUserFileVO).collect(Collectors.toList());
    }
    // ******************************** private ********************************

    /**
     * 执行文件下载的动作
     * <p>
     * 1、查询文件的真实存储路径
     * 2、添加跨域的公共响应头
     * 3、拼装下载文件的名称、长度等等响应信息
     * 4、委托文件存储引擎去读取文件内容到响应的输出流中
     *
     * @param record
     * @param response
     */
    private void doDownload(RPanUserFile record, HttpServletResponse response) {
        RPanFile realFileRecord = iFileService.getById(record.getRealFileId());
        if (Objects.isNull(realFileRecord)) {
            throw new RPanBusinessException("当前的文件记录不存在");
        }
        addCommonResponseHeader(response, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        addDownloadAttribute(response, record, realFileRecord);
        realFile2OutputStream(realFileRecord.getRealPath(), response);
    }


    /**
     * 委托文件存储引擎去读取文件内容并写入到输出流中
     *
     * @param realPath
     * @param response
     */
    private void realFile2OutputStream(String realPath, HttpServletResponse response) {
        try {
            ReadFileContext context = new ReadFileContext();
            context.setRealPath(realPath);
            context.setOutputStream(response.getOutputStream());
            storageEngine.readFile(context);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPanBusinessException("文件下载失败");
        }
    }

    /**
     * 递归查询所有的子文件列表
     * 忽略是否删除的标识
     *
     * @param result
     * @param record
     */
    private void doFindAllChildRecords(List<RPanUserFile> result, RPanUserFile record) {
        if (Objects.isNull(record)) {
            return;
        }
        if (!checkIsFolder(record)) {
            return;
        }
        List<RPanUserFile> childRecords = findChildRecordsIgnoreDelFlag(record.getFileId());
        if (CollectionUtils.isEmpty(childRecords)) {
            return;
        }
        result.addAll(childRecords);
        childRecords.stream()
                .filter(childRecord -> FolderFlagEnum.YES.getCode().equals(childRecord.getFolderFlag()))
                .forEach(childRecord -> doFindAllChildRecords(result, childRecord));
    }

    /**
     * 查询文件夹下面的文件记录，忽略删除标识
     *
     * @param fileId
     * @return
     */
    private List<RPanUserFile> findChildRecordsIgnoreDelFlag(Long fileId) {
        LambdaQueryWrapper<RPanUserFile> wrapper = Wrappers.<RPanUserFile>lambdaQuery();
        wrapper.eq(RPanUserFile::getParentId, fileId);
        return list(wrapper);
    }

    /**
     * 搜索的后置操作
     * 1. 发布文件搜索的事件
     */
    private void afterSearch(FileSearchContext context) {
        UserSearchEvent event = new UserSearchEvent(this, context.getKeyword(), context.getUserId());
        this.applicationContext.publishEvent(event);
    }

    /**
     * 填充父文件夹名称
     *
     * @param result
     */
    private void fillParentFilename(List<FileSearchResultVO> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        List<Long> parentIdList = result.stream().map(FileSearchResultVO::getParentId).collect(Collectors.toList());
        List<RPanUserFile> parentRecords = listByIds(parentIdList);
        Map<Long, String> fileId2FilenameMap = parentRecords.stream()
                .collect(Collectors.toMap(RPanUserFile::getFileId, RPanUserFile::getFilename));
        result.stream().forEach(vo -> vo.setParentFilename(fileId2FilenameMap.get(vo.getParentId())));
    }

    /**
     * 搜索文件列表
     *
     * @param context
     * @return
     */
    private List<FileSearchResultVO> doSearch(FileSearchContext context) {
        return this.baseMapper.searchFile(context);
    }

    /**
     * 执行文件复制的动作
     *
     * @param context
     */
    private void doCopy(CopyFileContext context) {
        List<RPanUserFile> prepareRecords = context.getPrepareRecords();
        if (CollectionUtils.isNotEmpty(prepareRecords)) {
            List<RPanUserFile> allRecords = new ArrayList<>();

            prepareRecords.stream().forEach(record -> this.assembleCopyChildRecord(
                    allRecords, record, context.getTargetParentId(), context.getUserId()
            ));


            if (!this.saveBatch(allRecords)) {
                throw new RPanBusinessException("文件复制失败");
            }
        }

    }

    /**
     * 拼装当前文件记录以及所有的子文件记录
     *
     * @param allRecords
     * @param record
     * @param targetParentId
     * @param userId
     */
    private void assembleCopyChildRecord(List<RPanUserFile> allRecords, RPanUserFile record, Long targetParentId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Long newFileId = IdUtil.get();
        Long oldFileId = record.getFileId();

        record.setParentId(targetParentId);
        record.setFileId(newFileId);
        record.setUserId(userId);
        record.setCreateTime(now);
        record.setUpdateTime(now);
        record.setCreateUser(userId);
        record.setUpdateUser(userId);
        this.handleDuplicateFilename(record);

        allRecords.add(record);
        if (this.checkIsFolder(record)) {
            List<RPanUserFile> childRecords = this.findChildRecords(oldFileId);
            if (CollectionUtils.isEmpty(childRecords)) {
                return;
            }
            childRecords.stream().forEach(childRecord -> this.assembleCopyChildRecord(allRecords, childRecord, newFileId, userId));
        }

    }

    /**
     * 查找下一级的文件记录
     *
     * @param parentId
     * @return
     */
    private List<RPanUserFile> findChildRecords(Long parentId) {
        LambdaQueryWrapper<RPanUserFile> wrapper = Wrappers.<RPanUserFile>lambdaQuery()
                .eq(RPanUserFile::getParentId, parentId)
                .eq(RPanUserFile::getDelFlag, DelFlagEnum.NO.getCode());
        return this.list(wrapper);
    }

    /**
     * @param context
     */
    private void checkCopyCondition(CopyFileContext context) {
        Long targetParentId = context.getTargetParentId();
        if (!this.checkIsFolder(this.getById(targetParentId))) {
            throw new RPanBusinessException("目标不是一个文件夹");
        }

        List<Long> fileIdList = context.getFileIdList();
        List<RPanUserFile> prepareRecords = this.listByIds(fileIdList);
        context.setPrepareRecords(prepareRecords);
        if (this.checkIsChildFolder(prepareRecords, targetParentId, context.getUserId())) {
            throw new RPanBusinessException("目标文件夹不能是选中文件夹的子文件夹");
        }
    }

    /**
     * 执行文件转移的动作
     *
     * @param context
     */
    private void doTransfer(TransferFileContext context) {
        List<RPanUserFile> prepareRecords = context.getPrepareRecords();
        for (RPanUserFile record : prepareRecords) {
            record.setParentId(context.getTargetParentId());
            record.setUserId(context.getUserId());
            LocalDateTime now = LocalDateTime.now();
            record.setCreateTime(now);
            record.setUpdateTime(now);
            record.setCreateUser(context.getUserId());
            record.setUpdateUser(context.getUserId());
            this.handleDuplicateFilename(record);
        }
        if (!this.updateBatchById(prepareRecords)) {
            throw new RPanBusinessException("文件转移失败");
        }
    }

    /**
     * 文件转移的权限校验
     * 1. 目标文件必须是一个文件夹
     * 2. 选中的文件中不能含有目标文件夹以及其子文件夹
     *
     * @param context
     */
    private void checkTransferCondition(TransferFileContext context) {
        Long targetParentId = context.getTargetParentId();
        if (!this.checkIsFolder(this.getById(targetParentId))) {
            throw new RPanBusinessException("目标不是一个文件夹");
        }

        List<Long> fileIdList = context.getFileIdList();
        List<RPanUserFile> prepareRecords = this.listByIds(fileIdList);
        context.setPrepareRecords(prepareRecords);
        if (this.checkIsChildFolder(prepareRecords, targetParentId, context.getUserId())) {
            throw new RPanBusinessException("目标文件夹不能是选中文件夹的子文件夹");
        }

    }

    /**
     * 校验目标文件夹id是否是要操作的文件记录的子文件夹id以及其子文件夹id
     * 1. 如果要操作的文件列表中没有文件夹, 那就直接返回false
     * 2. 拼装文件夹id以及所有的子文件夹id, 判断存在即可
     *
     * @param prepareRecords
     * @param targetParentId
     * @param userId
     * @return
     */
    private boolean checkIsChildFolder(List<RPanUserFile> prepareRecords, Long targetParentId, Long userId) {
        prepareRecords = prepareRecords.stream()
                .filter(record -> Objects.equals(record.getFolderFlag(), FolderFlagEnum.YES.getCode()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(prepareRecords)) {
            return false;
        }
        List<RPanUserFile> folderRecords = this.queryFolderRecords(userId);
        Map<Long, List<RPanUserFile>> folderRecordMap = folderRecords.stream().collect(Collectors.groupingBy(RPanUserFile::getParentId));
        List<RPanUserFile> unavailableRecords = Collections.emptyList();
        unavailableRecords.addAll(prepareRecords);
        prepareRecords.stream().forEach(record -> this.findAllChildFolderRecords(unavailableRecords, folderRecordMap, record));

        List<Long> unavailableFolderRecordIds = unavailableRecords.stream().map(RPanUserFile::getFileId).collect(Collectors.toList());

        return unavailableFolderRecordIds.contains(targetParentId);
    }

    /**
     * 查找文件夹的所有子文件夹记录
     *
     * @param unavailableFolderRecords
     * @param folderRecordMap
     * @param record
     */
    private void findAllChildFolderRecords(List<RPanUserFile> unavailableFolderRecords, Map<Long, List<RPanUserFile>> folderRecordMap, RPanUserFile record) {
        if (Objects.isNull(record)) {
            return;
        }
        List<RPanUserFile> childFolderRecords = folderRecordMap.get(record.getFileId());
        if (CollectionUtils.isEmpty(childFolderRecords)) {
            return;
        }
        unavailableFolderRecords.addAll(childFolderRecords);
        childFolderRecords.stream().forEach(childRecord -> findAllChildFolderRecords(unavailableFolderRecords, folderRecordMap, childRecord));
    }

    /**
     * 拼装文件夹树列表
     *
     * @param folderRecords
     * @return
     */
    private List<FolderTreeNodeVO> assembleFolderTreeNodeVOList(List<RPanUserFile> folderRecords) {
        if (CollectionUtils.isEmpty(folderRecords)) {
            return Collections.emptyList();
        }

        List<FolderTreeNodeVO> mappedFolderTreeNodeVOList = folderRecords.stream()
                .map(fileConverter::rPanUserFile2FolderTreeNodeVO).collect(Collectors.toList());
        Map<Long, List<FolderTreeNodeVO>> mappedFolderTreeNodeVOMap = mappedFolderTreeNodeVOList.stream()
                .collect(Collectors.groupingBy(FolderTreeNodeVO::getParentId));

        for (FolderTreeNodeVO node : mappedFolderTreeNodeVOList) {
            List<FolderTreeNodeVO> children = mappedFolderTreeNodeVOMap.get(node.getId());
            if (CollectionUtils.isNotEmpty(children)) {
                List<FolderTreeNodeVO> nodeChildren = node.getChildren();
                nodeChildren.addAll(children);
            }
        }

        return mappedFolderTreeNodeVOList.stream()
                .filter(node -> Objects.equals(node.getParentId(), 0L)).collect(Collectors.toList());
    }

    /**
     * 查询用户所有有效的文件夹信息
     *
     * @param userId
     * @return
     */
    private List<RPanUserFile> queryFolderRecords(Long userId) {
        LambdaQueryWrapper<RPanUserFile> wrapper = Wrappers.<RPanUserFile>lambdaQuery()
                .eq(RPanUserFile::getUserId, userId)
                .eq(RPanUserFile::getFolderFlag, FolderFlagEnum.YES.getCode())
                .eq(RPanUserFile::getDelFlag, DelFlagEnum.NO.getCode());
        return this.list(wrapper);
    }

    /**
     * 执行文件预览的动作
     * 1. 查询文件的真实存储路径
     * 2. 添加跨域的公共响应头
     * 3. 委托文件存储引擎去读取文件内容到响应的输出流中
     *
     * @param record
     * @param response
     */
    private void doPreview(RPanUserFile record, HttpServletResponse response) {
        RPanFile realFileRecord = Optional.ofNullable(this.iFileService.getById(record.getRealFileId()))
                .orElseThrow(() -> new RPanBusinessException("文件不存在"));
        this.addCommonResponseHeader(response, realFileRecord.getFilePreviewContentType());
        this.readFile2OutputStream(realFileRecord.getRealPath(), response);
    }

    /**
     * 下载文件
     * 1. 查询文件的真实存储路径
     * 2. 添加跨域的公共响应头
     * 3. 拼装下载文件的名称, 长度等等响应信息
     * 4. 委托文件存储引擎去读取文件内容到响应的输出流中
     *
     * @param record
     * @param response
     */
    private void doDownLoad(RPanUserFile record, HttpServletResponse response) {
        RPanFile realFileRecord = Optional.ofNullable(this.iFileService.getById(record.getRealFileId()))
                .orElseThrow(() -> new RPanBusinessException("文件不存在"));
        this.addCommonResponseHeader(response, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        this.addDownloadAttribute(response, record, realFileRecord);
        this.readFile2OutputStream(realFileRecord.getRealPath(), response);
    }

    /**
     * 委托文件存储引擎读取文件内容, 并写入到输出流中
     *
     * @param realPath
     * @param response
     */
    private void readFile2OutputStream(String realPath, HttpServletResponse response) {
        try {
            ReadFileContext context = new ReadFileContext();
            context.setRealPath(realPath);
            context.setOutputStream(response.getOutputStream());
            this.storageEngine.readFile(context);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPanBusinessException("文件下载失败");
        }
    }

    /**
     * 添加文件下载的属性信息
     *
     * @param response
     * @param record
     * @param realFileRecord
     */
    private void addDownloadAttribute(HttpServletResponse response, RPanUserFile record, RPanFile realFileRecord) {
        try {
            response.addHeader(FileConsts.CONTENT_DISPOSITION_STR,
                    FileConsts.CONTENT_DISPOSITION_VALUE_PREFIX_STR +
                            new String(record.getFilename().getBytes(FileConsts.GB2312_STR), FileConsts.ISO_8859_1_STR));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RPanBusinessException("文件下载失败");
        }
        response.setContentLengthLong(Long.parseLong(realFileRecord.getFileSize()));
    }

    /**
     * 添加公共的文件流响应头
     *
     * @param response
     * @param contentType
     */
    private void addCommonResponseHeader(HttpServletResponse response, String contentType) {
        response.reset();
        HttpUtil.addCorsResponseHeaders(response);
        response.addHeader(FileConsts.CONTENT_TYPE_STR, contentType);
        response.setContentType(contentType);
    }

    /**
     * 检查当前文件记录是否为文件夹
     *
     * @param record
     * @return
     */
    private boolean checkIsFolder(RPanUserFile record) {
        if (record == null) {
            throw new RPanBusinessException("文件不存在");
        }
        return FolderFlagEnum.YES.getCode().equals(record.getFolderFlag());
    }

    /**
     * 校验用户的操作权限
     * 1. 文件记录必须存在
     * 2. 文件记录的创建者
     *
     * @param record
     * @param userId
     */
    private void checkOperatePermission(RPanUserFile record, Long userId) {
        if (record == null) {
            throw new RPanBusinessException("文件不存在");
        }
        if (!Objects.equals(userId, record.getCreateUser())) {
            throw new RPanBusinessException("无权操作该文件");
        }
    }

    /**
     * 合并文件分片并保存物理文件记录
     *
     * @param context
     */
    private void mergeFileChunkAndSaveFile(FileChunkMergeContext context) {
        FileChunkMergeAndSaveContext anotherContext =
                this.fileConverter.fileChunkMergeContext2FileChunkMergeAndSaveContext(context);
        this.iFileService.mergeFileChunkAndSaveFile(anotherContext);
        context.setRecord(anotherContext.getRecord());
    }

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
    private RPanFile getFileByUserIdAndIdentifier(SecUploadFileContext context) {

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
        entity.setUpdateTime(LocalDateTime.now());
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
        entity.setUpdateUser(userId);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

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

        long count = getDuplicateFilename(entity, newFilenameWithoutSuffix);
        if (count == 0L) {
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
    private String assembleNewFilename(String newFilenameWithoutSuffix, long count, String newFilenameSuffix) {
        return new StringBuilder(newFilenameWithoutSuffix)
                .append(FileConsts.CN_LEFT_PARENTHESIS_STR)
                .append(count)
                .append(FileConsts.CN_RIGHT_PARENTHESIS_STR)
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
    private long getDuplicateFilename(RPanUserFile entity, String newFilenameWithoutSuffix) {

        LambdaQueryWrapper<RPanUserFile> wrapper = Wrappers.<RPanUserFile>lambdaQuery()
                .eq(RPanUserFile::getParentId, entity.getParentId())
                .eq(RPanUserFile::getFolderFlag, entity.getFolderFlag())
                .eq(RPanUserFile::getDelFlag, DelFlagEnum.NO)
                .likeLeft(RPanUserFile::getFilename, newFilenameWithoutSuffix);

        return this.count(wrapper);
    }

}




