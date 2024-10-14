package com.imooc.pan.server.modules.file.controller;

import com.imooc.pan.core.constants.GlobalConst;
import com.imooc.pan.core.response.R;
import com.imooc.pan.core.utils.EntityIdUtil;
import com.imooc.pan.server.common.utils.UrlUtil;
import com.imooc.pan.server.common.utils.UserIdUtil;
import com.imooc.pan.server.modules.file.constants.DelFlagEnum;
import com.imooc.pan.server.modules.file.constants.FileConsts;
import com.imooc.pan.server.modules.file.context.*;
import com.imooc.pan.server.modules.file.converter.FileConverter;
import com.imooc.pan.server.modules.file.po.*;
import com.imooc.pan.server.modules.file.service.IUserFileService;
import com.imooc.pan.server.modules.file.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 文件模块控制器
 */
@Tag("文件模块")
@Slf4j
@Validated
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private IUserFileService iUserFileService;

    @Resource
    private FileConverter fileConverter;

    @Operation(
            summary = "查询文件列表",
            description = "该接口提供了按照父文件夹id和文件类型, 查询文件列表的功能"
    )
    @GetMapping("/list")
    public R<List<RPanUserFileVO>> list(
            @NotBlank(message = "父文件夹id不能为空") @RequestParam(required = false) String parentId,
            @RequestParam(required = false, defaultValue = FileConsts.ALL_FILE_TYPE) String fileTypes) {

        long realParentId;
        // 临时处理: 如果前端的父文件夹传了 -1 或 0, 都视为0
        if ("-1".equals(parentId) || "0".equals(parentId)) {
            realParentId = 0L;
        } else {
            realParentId = EntityIdUtil.decrypt(UrlUtil.decodeIfNeeds(parentId));
        }

        List<Integer> fileTypeArray = null;
        if (!Objects.equals(FileConsts.ALL_FILE_TYPE, fileTypes)) {
            fileTypeArray = Arrays.stream(fileTypes.split(GlobalConst.COMMON_SEPARATOR))
                    .map(Integer::parseInt).toList();
        }

        QueryFileListContext context = new QueryFileListContext();
        context.setParentId(realParentId);
        context.setFileTypeArray(fileTypeArray);
        context.setUserId(UserIdUtil.get());
        context.setDelFlag(DelFlagEnum.NO.getCode());

        List<RPanUserFileVO> fileList = this.iUserFileService.getFileList(context);
        return R.data(fileList);
    }

    @Operation(
            summary = "创建文件夹",
            description = "该接口提供了创建文件夹的功能"
    )
    @PostMapping("/folder")
    public R<String> createFolder(@Validated @RequestBody CreateFolderPO po) {
        CreateFolderContext context = this.fileConverter.convertPO2Context(po);
        Long fileId = this.iUserFileService.createFolder(context);
        return R.data(EntityIdUtil.encrypt(fileId));
    }

    @Operation(
            summary = "文件重命名",
            description = "该接口提供了文件重命名的功能"
    )
    @PutMapping
    public R<Object> updateFilename(@Validated @RequestBody UpdateFilenamePO updateFilenamePO) {
        UpdateFilenameContext context = this.fileConverter.convertPO2Context(updateFilenamePO);
        this.iUserFileService.updateFilename(context);
        return R.success();
    }

    @Operation(
            summary = "批量删除文件",
            description = "该接口提供了批量删除文件的功能"
    )
    @DeleteMapping
    public R<Object> deleteFile(@Validated @RequestBody DeleteFilePO deleteFilePO) {
        DeleteFileContext context = this.fileConverter.convertPO2Context(deleteFilePO);

        String fileIds = deleteFilePO.getFileIds();
        List<Long> fileIdList = Arrays.stream(fileIds.split(GlobalConst.COMMON_SEPARATOR))
                .map(EntityIdUtil::decrypt).toList();
        context.setFileIdList(fileIdList);

        this.iUserFileService.deleteFile(context);
        return R.success();
    }

    @Operation(
            summary = "文件秒传",
            description = "该接口提供了文件秒传的功能"
    )
    @PostMapping("/sec-upload")
    public R<Object> secUpload(@Validated @RequestBody SecUploadPO secUploadPO) {
        SecUploadFileContext context = this.fileConverter.convertPO2Context(secUploadPO);
        boolean success = this.iUserFileService.secUpload(context);
        if (success) {
            return R.success();
        } else {
            return R.fail("文件唯一标识不存在, 请手动执行文件上传的操作");
        }
    }

    @Operation(
            summary = "单文件上传",
            description = "该接口提供了单文件上传的功能"
    )
    @PostMapping("/upload")
    public R<Object> upload(@Validated FileUploadPO fileUploadPO) {
        FileUploadContext context = this.fileConverter.convertPO2Context(fileUploadPO);
        this.iUserFileService.upload(context);
        return R.success();
    }


    @Operation(
            summary = "文件分片上传",
            description = "该接口提供了文件分片上传的功能"
    )
    @PostMapping("/chunk-upload")
    public R<FileChunkUploadVO> chunkUpload(@Validated FileChunkUploadPO fileChunkUploadPO) {
        FileChunkUploadContext context = this.fileConverter.convertPO2Context(fileChunkUploadPO);
        FileChunkUploadVO fileChunkUploadVO = this.iUserFileService.chunkUpload(context);
        return R.data(fileChunkUploadVO);
    }


    @Operation(
            summary = "查询文件分片",
            description = "该接口提供了查询文件分片的功能"
    )
    @GetMapping("/chunk-upload")
    public R<UploadedChunksVO> getUploadedChunks(@Validated QueryUploadedChunksPO queryUploadedChunksPO) {
        QueryUploadedChunksContext context = this.fileConverter.convertPO2Context(queryUploadedChunksPO);
        UploadedChunksVO vo = this.iUserFileService.getUploadedChunks(context);
        return R.data(vo);
    }

    @Operation(
            summary = "合并文件分片",
            description = "该接口提供了合并文件分片的功能"
    )
    @PostMapping("/merge")
    public R<Object> mergeFile(@Validated @RequestBody FileChunkMergePO fileChunkMergePO) {
        FileChunkMergeContext context = this.fileConverter.convertPO2Context(fileChunkMergePO);
        this.iUserFileService.mergeFile(context);
        return R.success();
    }

    @Operation(
            summary = "下载文件",
            description = "该接口提供了下载文件的功能"
    )
    @GetMapping("/download")
    public void download(@Validated @NotBlank(message = "文件id不能为空") @RequestParam(required = false) String fileId,
                         HttpServletResponse response) {
        FileDownloadContext context = new FileDownloadContext(EntityIdUtil.decrypt(fileId), response, UserIdUtil.get());
        this.iUserFileService.download(context);
    }


    @Operation(
            summary = "预览文件",
            description = "该接口提供了预览文件的功能"
    )
    @GetMapping("/preview")
    public void preview(@Validated @NotBlank(message = "文件id不能为空") @RequestParam(required = false) String fileId,
                        HttpServletResponse response) {
        FilePreviewContext context = new FilePreviewContext(EntityIdUtil.decrypt(fileId), response, UserIdUtil.get());
        this.iUserFileService.preview(context);
    }

    @Operation(
            summary = "查询文件夹树",
            description = "该接口提供了查询文件夹树的功能"
    )
    @GetMapping("/folder/tree")
    public R<List<FolderTreeNodeVO>> getFolderTree() {
        QueryFolderTreeContext context = new QueryFolderTreeContext();
        context.setUserId(UserIdUtil.get());
        List<FolderTreeNodeVO> result = this.iUserFileService.getFolderTree(context);
        return R.data(result);
    }

    @Operation(
            summary = "文件转移",
            description = "该接口提供了文件转移的功能"
    )
    @PostMapping("/transfer")
    public R<Object> getFolderTree(@Validated @RequestBody TransferFilePO transferFilePO) {
        TransferFileContext context = this.fileConverter.convertPO2Context(transferFilePO);
        this.iUserFileService.transfer(context);
        return R.success();
    }

    @Operation(
            summary = "文件复制",
            description = "该接口提供了文件复制的功能"
    )
    @PostMapping("/copy")
    public R<Object> getFolderTree(@Validated @RequestBody CopyFilePO copyFilePO) {
        CopyFileContext context = this.fileConverter.convertPO2Context(copyFilePO);
        this.iUserFileService.copy(context);
        return R.success();
    }

    @Operation(
            summary = "文件搜索",
            description = "该接口提供了文件搜索的功能"
    )
    @GetMapping("/search")
    public R<List<FileSearchResultVO>> search(@Validated FileSearchPO fileSearchPO) {
        FileSearchContext context = this.fileConverter.convertPO2Context(fileSearchPO);
        List<FileSearchResultVO> result = this.iUserFileService.search(context);
        return R.data(result);
    }

    @Operation(
            summary = "查询面包屑列表",
            description = "该接口提供了查询面包屑列表的功能"
    )
    @GetMapping("/breadcrumbs")
    public R<List<BreadcrumbVO>> getBreadcrumbs(
            @NotBlank(message = "文件id不能为空") @RequestParam(required = false) String fileId) {
        QueryBreadcrumbsContext context = new QueryBreadcrumbsContext();
        String decoded = UrlUtil.decodeIfNeeds(fileId);
        context.setFileId(EntityIdUtil.decrypt(decoded));
        context.setUserId(UserIdUtil.get());
        List<BreadcrumbVO> result = this.iUserFileService.getBreadcrumbs(context);
        return R.data(result);
    }


}
