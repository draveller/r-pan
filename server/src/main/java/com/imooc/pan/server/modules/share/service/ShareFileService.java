package com.imooc.pan.server.modules.share.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.server.modules.share.context.SaveShareFilesContext;
import com.imooc.pan.server.modules.share.entity.RPanShareFile;
import com.imooc.pan.server.modules.share.mapper.RPanShareFileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author imooc
 * @description 针对表【r_pan_share_file(用户分享文件表)】的数据库操作Service实现
 * @createDate 2022-11-09 18:38:38
 */
@Service
public class ShareFileService extends ServiceImpl<RPanShareFileMapper, RPanShareFile> implements IService<RPanShareFile> {

    /**
     * 保存分享的文件的对应关系
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveShareFiles(SaveShareFilesContext context) {
        Long shareId = context.getShareId();
        List<Long> shareFileIdList = context.getShareFileIdList();
        Long userId = context.getUserId();

        List<RPanShareFile> records = Lists.newArrayList();

        for (Long shareFileId : shareFileIdList) {
            RPanShareFile fileRecord = new RPanShareFile();
            fileRecord.setShareId(shareId);
            fileRecord.setFileId(shareFileId);
            fileRecord.setCreateUser(userId);
            records.add(fileRecord);
        }

        if (!saveBatch(records)) {
            throw new RPanBusinessException("保存文件分享关联关系失败");
        }
    }

}




