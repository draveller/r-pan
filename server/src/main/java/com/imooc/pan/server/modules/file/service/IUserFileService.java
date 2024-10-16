package com.imooc.pan.server.modules.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pan.server.modules.file.context.*;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.server.modules.file.vo.*;

import java.util.List;

/**
 * @author 18063
 * @description 针对表【r_pan_user_file(用户文件信息表)】的数据库操作Service
 * @createDate 2024-09-28 14:11:44
 */
public interface IUserFileService extends IService<RPanUserFile> {

    /**
     * 创建文件夹信息
     *
     * @param context 上下文字段集合对象
     * @return 文件夹id
     */
    Long createFolder(CreateFolderContext context);

    RPanUserFile getUserRootFile(Long userId);

    List<RPanUserFileVO> getFileList(QueryFileListContext context);

    void updateFilename(UpdateFilenameContext context);

    void deleteFile(DeleteFileContext context);

    boolean secUpload(SecUploadFileContext context);

    void upload(FileUploadContext context);

    FileChunkUploadVO chunkUpload(FileChunkUploadContext context);

    UploadedChunksVO getUploadedChunks(QueryUploadedChunksContext context);

    void mergeFile(FileChunkMergeContext context);

    void download(FileDownloadContext context);

    void preview(FilePreviewContext context);

    List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext context);

    void transfer(TransferFileContext context);

    void copy(CopyFileContext context);

    List<FileSearchResultVO> search(FileSearchContext context);

    List<BreadcrumbVO> getBreadcrumbs(QueryBreadcrumbsContext context);


    /**
     * 递归查询所有的子文件信息
     *
     * @param records
     * @return
     */
    List<RPanUserFile> findAllFileRecords(List<RPanUserFile> records);

    void downloadWithoutCheckUser(FileDownloadContext fileDownloadContext);

    /**
     * 递归查询所有的子文件信息
     *
     * @param fileIdList
     * @return
     */
    List<RPanUserFile> findAllFileRecordsByFileIdList(List<Long> fileIdList);

    /**
     * 实体转换
     *
     * @param records
     * @return
     */
    List<RPanUserFileVO> transferVOList(List<RPanUserFile> records);
}
