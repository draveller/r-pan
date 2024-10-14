package com.imooc.pan.server.modules.share.controller;

import com.google.common.base.Splitter;
import com.imooc.pan.core.constants.GlobalConst;
import com.imooc.pan.core.response.R;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.server.common.annotation.NoCheckLogin;
import com.imooc.pan.server.common.annotation.CheckShareCode;
import com.imooc.pan.server.common.utils.ShareIdUtil;
import com.imooc.pan.server.common.utils.UserIdUtil;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;
import com.imooc.pan.server.modules.share.context.*;
import com.imooc.pan.server.modules.share.converter.ShareConverter;
import com.imooc.pan.server.modules.share.po.CancelSharePO;
import com.imooc.pan.server.modules.share.po.CheckShareCodePO;
import com.imooc.pan.server.modules.share.po.CreateShareUrlPO;
import com.imooc.pan.server.modules.share.po.ShareSavePO;
import com.imooc.pan.server.modules.share.service.ShareService;
import com.imooc.pan.server.modules.share.vo.RPanShareUrlListVO;
import com.imooc.pan.server.modules.share.vo.RPanShareUrlVO;
import com.imooc.pan.server.modules.share.vo.ShareDetailVO;
import com.imooc.pan.server.modules.share.vo.ShareSimpleDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag("分享模块")
@Validated
@RestController
@RequestMapping("/share")
public class ShareController {

    @Resource
    private ShareService shareService;

    @Resource
    private ShareConverter shareConverter;

    @Operation(
            summary = "创建分享链接",
            description = "该接口提供了创建分享链接的功能"
    )
    @PostMapping
    public R<RPanShareUrlVO> create(@Validated @RequestBody CreateShareUrlPO createShareUrlPO) {
        CreateShareUrlContext context = shareConverter.convertPO2Context(createShareUrlPO);

        String shareFileIds = createShareUrlPO.getShareFileIds();
        List<Long> shareFileIdList = Splitter.on(GlobalConst.COMMON_SEPARATOR)
                .splitToList(shareFileIds).stream().map(IdUtil::decrypt).toList();

        context.setShareFileIdList(shareFileIdList);

        RPanShareUrlVO vo = shareService.create(context);
        return R.data(vo);
    }

    @Operation(
            summary = "查询分享链接列表",
            description = "该接口提供了查询分享链接列表的功能"
    )
    @GetMapping("/list")
    public R<List<RPanShareUrlListVO>> getShares() {
        QueryShareListContext context = new QueryShareListContext();
        context.setUserId(UserIdUtil.get());
        List<RPanShareUrlListVO> result = shareService.getShares(context);
        return R.data(result);
    }

    @Operation(
            summary = "取消分享",
            description = "该接口提供了取消分享的功能"
    )
    @DeleteMapping
    public R cancelShare(@Validated @RequestBody CancelSharePO cancelSharePO) {
        CancelShareContext context = new CancelShareContext();

        context.setUserId(UserIdUtil.get());

        String shareIds = cancelSharePO.getShareIds();
        List<Long> shareIdList = Splitter.on(GlobalConst.COMMON_SEPARATOR)
                .splitToList(shareIds).stream().map(IdUtil::decrypt).toList();
        context.setShareIdList(shareIdList);

        shareService.cancelShare(context);
        return R.success();
    }

    @Operation(
            summary = "校验分享码",
            description = "该接口提供了校验分享码的功能"
    )
    @NoCheckLogin
    @PostMapping("/code/check")
    public R<String> checkShareCode(@Validated @RequestBody CheckShareCodePO checkShareCodePO) {
        CheckShareCodeContext context = new CheckShareCodeContext();

        context.setShareId(IdUtil.decrypt(checkShareCodePO.getShareId()));
        context.setShareCode(checkShareCodePO.getShareCode().trim());

        String token = shareService.checkShareCode(context);
        return R.data(token);
    }

    @Operation(
            summary = "查询分享的详情",
            description = "该接口提供了查询分享的详情的功能"
    )
    @NoCheckLogin
    @CheckShareCode
    @GetMapping
    public R<ShareDetailVO> detail() {
        QueryShareDetailContext context = new QueryShareDetailContext();
        context.setShareId(ShareIdUtil.get());
        ShareDetailVO vo = shareService.detail(context);
        return R.data(vo);
    }

    @Operation(
            summary = "查询分享的简单详情",
            description = "该接口提供了查询分享的简单详情的功能"
    )
    @NoCheckLogin
    @GetMapping("/simple")
    public R<ShareSimpleDetailVO> simpleDetail(@NotBlank(message = "分享的ID不能为空")
                                               @RequestParam(value = "shareId", required = false) String shareId) {
        QueryShareSimpleDetailContext context = new QueryShareSimpleDetailContext();
        context.setShareId(IdUtil.decrypt(shareId));
        ShareSimpleDetailVO vo = shareService.simpleDetail(context);
        return R.data(vo);
    }

    @Operation(
            summary = "获取下一级文件列表",
            description = "该接口提供了获取下一级文件列表的功能"
    )
    @GetMapping("/file/list")
    @CheckShareCode
    @NoCheckLogin
    public R<List<RPanUserFileVO>> fileList(@NotBlank(message = "文件的父ID不能为空")
                                            @RequestParam(value = "parentId", required = false) String parentId) {
        QueryChildFileListContext context = new QueryChildFileListContext();
        context.setShareId(ShareIdUtil.get());
        context.setParentId(IdUtil.decrypt(parentId));
        List<RPanUserFileVO> result = shareService.fileList(context);
        return R.data(result);
    }

    @Operation(
            summary = "保存至我的网盘",
            description = "该接口提供了保存至我的网盘的功能"
    )
    @CheckShareCode
    @PostMapping("/save")
    public R saveFiles(@Validated @RequestBody ShareSavePO shareSavePO) {
        ShareSaveContext context = new ShareSaveContext();

        String fileIds = shareSavePO.getFileIds();
        List<Long> fileIdList = Splitter.on(GlobalConst.COMMON_SEPARATOR)
                .splitToList(fileIds).stream().map(IdUtil::decrypt).toList();
        context.setFileIdList(fileIdList);

        context.setTargetParentId(IdUtil.decrypt(shareSavePO.getTargetParentId()));
        context.setUserId(UserIdUtil.get());
        context.setShareId(ShareIdUtil.get());

        shareService.saveFiles(context);
        return R.success();
    }

    @Operation(
            summary = "分享文件下载",
            description = "该接口提供了分享文件下载的功能"
    )
    @GetMapping("/file/download")
    @CheckShareCode
    public void download(@NotBlank(message = "文件ID不能为空")
                         @RequestParam(value = "fileId", required = false) String fileId,
                         HttpServletResponse response) {
        ShareFileDownloadContext context = new ShareFileDownloadContext();
        context.setFileId(IdUtil.decrypt(fileId));
        context.setShareId(ShareIdUtil.get());
        context.setUserId(UserIdUtil.get());
        context.setResponse(response);
        shareService.download(context);
    }

}
