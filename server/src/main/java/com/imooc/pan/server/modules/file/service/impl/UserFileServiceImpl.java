package com.imooc.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.core.constants.RPanConstants;
import com.imooc.pan.server.modules.file.constants.DelFlagEnum;
import com.imooc.pan.server.modules.file.constants.FileConstants;
import com.imooc.pan.server.modules.file.context.CreateFolderContext;
import com.imooc.pan.server.modules.file.context.QueryFileListContext;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.server.modules.file.enums.FolderFlagEnum;
import com.imooc.pan.server.modules.file.mapper.RPanUserFileMapper;
import com.imooc.pan.server.modules.file.service.IUserFileService;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 18063
 * @description 针对表【r_pan_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:11:44
 */
@Service
public class UserFileServiceImpl extends ServiceImpl<RPanUserFileMapper, RPanUserFile>
        implements IUserFileService {

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


    // ******************************** private ********************************

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




