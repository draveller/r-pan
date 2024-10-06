package com.imooc.pan.server.modules.recycle.controller;


import com.google.common.base.Splitter;
import com.imooc.pan.core.constants.RPanConstants;
import com.imooc.pan.core.response.R;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.server.common.utils.UserIdUtil;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;
import com.imooc.pan.server.modules.recycle.context.DeleteContext;
import com.imooc.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.imooc.pan.server.modules.recycle.context.RestoreContext;
import com.imooc.pan.server.modules.recycle.po.DeletePO;
import com.imooc.pan.server.modules.recycle.po.RestorePO;
import com.imooc.pan.server.modules.recycle.service.IRecycleService;
import io.swagger.v3.oas.annotations.Operation;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 回收站模块控制器
 */
@Tag("回收站模块")
@Validated
@RestController
public class RecycleController {

    @Autowired
    private IRecycleService iRecycleService;

    @Operation(
            summary = "获取回收站文件列表",
            description = "该接口提供了获取回收站文件列表的功能"
    )
    @GetMapping("/recycles")
    public R<List<RPanUserFileVO>> recycles() {
        QueryRecycleFileListContext context = new QueryRecycleFileListContext();
        context.setUserId(UserIdUtil.get());
        List<RPanUserFileVO> result = this.iRecycleService.recycles(context);
        return R.data(result);
    }

    @Operation(
            summary = "批量还原已删除的文件",
            description = "该接口提供了批量还原已删除的文件的功能"
    )
    @PutMapping("/recycle/restore")
    public R<Object> restore(@Validated @RequestBody RestorePO restorePO) {
        RestoreContext context = new RestoreContext();
        context.setUserId(UserIdUtil.get());
        String fileIds = restorePO.getFileIds();
        List<Long> fileIdList = Arrays.stream(fileIds.split(RPanConstants.COMMON_SEPARATOR))
                .map(IdUtil::decrypt).collect(Collectors.toList());
        context.setFileIdList(fileIdList);
        this.iRecycleService.restore(context);
        return R.success();
    }

    @Operation(
            summary = "删除的文件批量彻底删除",
            description = "该接口提供了删除的文件批量彻底删除的功能"
    )
    @DeleteMapping("/recycle")
    public R<Object> delete(@Validated @RequestBody DeletePO deletePO) {
        DeleteContext context = new DeleteContext();
        context.setUserId(UserIdUtil.get());

        String fileIds = deletePO.getFileIds();
        List<Long> fileIdList = Splitter.on(RPanConstants.COMMON_SEPARATOR)
                .splitToList(fileIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setFileIdList(fileIdList);

        iRecycleService.delete(context);
        return R.success();
    }

}
