package com.imooc.pan.server.modules.share.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.imooc.pan.bloom.filter.core.BloomFilter;
import com.imooc.pan.bloom.filter.core.BloomFilterManager;
import com.imooc.pan.core.constants.GlobalConst;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.response.ResponseCode;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.core.utils.JwtUtil;
import com.imooc.pan.core.utils.UUIDUtil;
import com.imooc.pan.server.common.cache.ManualCacheService;
import com.imooc.pan.server.common.config.PanServerProps;
import com.imooc.pan.server.common.event.log.PublishErrorLogEvent;
import com.imooc.pan.server.modules.file.constants.DelFlagEnum;
import com.imooc.pan.server.modules.file.constants.FileConsts;
import com.imooc.pan.server.modules.file.context.CopyFileContext;
import com.imooc.pan.server.modules.file.context.FileDownloadContext;
import com.imooc.pan.server.modules.file.context.QueryFileListContext;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.server.modules.file.service.IUserFileService;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;
import com.imooc.pan.server.modules.share.constants.ShareConsts;
import com.imooc.pan.server.modules.share.context.*;
import com.imooc.pan.server.modules.share.entity.RPanShare;
import com.imooc.pan.server.modules.share.entity.RPanShareFile;
import com.imooc.pan.server.modules.share.enums.ShareDayTypeEnum;
import com.imooc.pan.server.modules.share.enums.ShareStatusEnum;
import com.imooc.pan.server.modules.share.mapper.RPanShareMapper;
import com.imooc.pan.server.modules.share.vo.*;
import com.imooc.pan.server.modules.user.entity.RPanUser;
import com.imooc.pan.server.modules.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author imooc
 * @description 针对表【r_pan_share(用户分享表)】的数据库操作Service实现
 * @createDate 2022-11-09 18:38:38
 */
@Slf4j
@Service
public class ShareService extends ServiceImpl<RPanShareMapper, RPanShare> implements IService<RPanShare>, ApplicationContextAware {

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";
    @Resource
    private PanServerProps config;
    @Resource
    private ShareFileService shareFileService;
    @Resource
    private IUserFileService iUserFileService;
    @Resource
    private UserService userService;
    @Resource
    @Qualifier(value = "shareManualCacheService")
    private ManualCacheService<RPanShare> cacheService;
    @Resource
    private BloomFilterManager manager;
    @Setter
    private ApplicationContext applicationContext;

    /**
     * 创建分享链接
     * <p>
     * 1、拼装分享实体，保存到数据库
     * 2、保存分享和对应文件的关联关系
     * 3、拼装返回实体并返回
     */
    @Transactional(rollbackFor = RPanBusinessException.class)
    public RPanShareUrlVO create(CreateShareUrlContext context) {
        this.saveShare(context);
        this.saveShareFiles(context);
        RPanShareUrlVO vo = assembleShareVO(context);
        this.afterCreate(context);
        return vo;
    }


    /**
     * 查询用户的分享列表
     */
    public List<RPanShareUrlListVO> getShares(QueryShareListContext context) {
        return baseMapper.selectShareVOListByUserId(context.getUserId());
    }

    /**
     * 取消分享链接
     * <p>
     * 1、校验用户操作权限
     * 2、删除对应的分享记录
     * 3、删除对应的分享文件关联关系记录
     */
    @Transactional(rollbackFor = RPanBusinessException.class)
    public void cancelShare(CancelShareContext context) {
        checkUserCancelSharePermission(context);
        doCancelShare(context);
        doCancelShareFiles(context);
    }

    /**
     * 校验分享码
     * <p>
     * 1、检查分享的状态是不是正常
     * 2、校验分享的分享码是不是正确
     * 3、生成一个短时间的分享token 返回给上游
     */
    public String checkShareCode(CheckShareCodeContext context) {
        RPanShare entity = checkShareStatus(context.getShareId());
        context.setEntity(entity);
        doCheckShareCode(context);
        return generateShareToken(context);
    }

    /**
     * 查询分享的详情
     * <p>
     * 1、校验分享的状态
     * 2、初始化分享实体
     * 3、查询分享的主体信息
     * 4、查询分享的文件列表
     * 5、查询分享者的信息
     */
    public ShareDetailVO detail(QueryShareDetailContext context) {
        RPanShare entity = checkShareStatus(context.getShareId());
        context.setEntity(entity);
        initShareVO(context);
        assembleMainShareInfo(context);
        assembleShareFilesInfo(context);
        assembleShareUserInfo(context);
        return context.getVo();
    }

    /**
     * 查询分享的简单详情
     * <p>
     * 1、校验分享的状态
     * 2、初始化分享实体
     * 3、查询分享的主体信息
     * 4、查询分享者的信息
     */
    public ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context) {
        RPanShare entity = checkShareStatus(context.getShareId());
        context.setEntity(entity);
        initShareSimpleVO(context);
        assembleMainShareSimpleInfo(context);
        assembleShareSimpleUserInfo(context);
        return context.getVo();
    }

    /**
     * 获取下一级的文件列表
     * <p>
     * 1、校验分享的状态
     * 2、校验文件的ID实在分享的文件列表中
     * 3、查询对应文件的子文件列表，返回
     */
    public List<RPanUserFileVO> fileList(QueryChildFileListContext context) {
        RPanShare entity = checkShareStatus(context.getShareId());
        context.setEntity(entity);
        List<RPanUserFileVO> allUserFileRecords = checkFileIdIsOnShareStatusAndGetAllShareUserFiles(context.getShareId(), Lists.newArrayList(context.getParentId()));
        Map<Long, List<RPanUserFileVO>> parentIdFileListMap = allUserFileRecords.stream().collect(Collectors.groupingBy(RPanUserFileVO::getParentId));
        List<RPanUserFileVO> rPanUserFileVOS = parentIdFileListMap.get(context.getParentId());
        if (CollectionUtils.isEmpty(rPanUserFileVOS)) {
            return Lists.newArrayList();
        }
        return rPanUserFileVOS;
    }

    /**
     * 转存至我的网盘
     * <p>
     * 1、校验分享状态
     * 2、校验文件ID是否合法
     * 3、执行保存我的网盘动作
     */
    public void saveFiles(ShareSaveContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdIsOnShareStatus(context.getShareId(), context.getFileIdList());
        doSaveFiles(context);
    }

    /**
     * 分享的文件下载
     * <p>
     * 1、校验分享状态
     * 2、校验文件ID的合法性
     * 3、执行文件下载的动作
     */
    public void download(ShareFileDownloadContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdIsOnShareStatus(context.getShareId(), Lists.newArrayList(context.getFileId()));
        doDownload(context);
    }

    /**
     * 刷新受影响的对应的分享的状态
     * <p>
     * 1、查询所有受影响的分享的ID集合
     * 2、去判断每一个分享对应的文件以及所有的父文件信息均为正常，该种情况，把分享的状态变为正常
     * 3、如果有分享的文件或者是父文件信息被删除，变更该分享的状态为有文件被删除
     */
    public void refreshShareStatus(List<Long> allAvailableFileIdList) {
        List<Long> shareIdList = getShareIdListByFileIdList(allAvailableFileIdList);
        if (CollectionUtils.isEmpty(shareIdList)) {
            return;
        }
        Set<Long> shareIdSet = Sets.newHashSet(shareIdList);
        shareIdSet.forEach(this::refreshOneShareStatus);
    }

    public List<Long> rollingQueryShareId(long startId, long limit) {
        return baseMapper.rollingQueryShareId(startId, limit);
    }

    // -------------------------------- private --------------------------------


    /**
     * 创建分享链接后置处理
     */
    private void afterCreate(CreateShareUrlContext context) {
        BloomFilter<Long> filter = manager.getFilter(BLOOM_FILTER_NAME);
        if (filter != null) {
            filter.put(context.getEntity().getId());
            log.info("已创建分享链接并存入布隆过滤器, 分享ID = {}", context.getEntity().getId());
        }
    }

    /**
     * 刷新一个分享的分享状态
     * <p>
     * 1、查询对应的分享信息，判断有效
     * 2、 去判断该分享对应的文件以及所有的父文件信息均为正常，该种情况，把分享的状态变为正常
     * 3、如果有分享的文件或者是父文件信息被删除，变更该分享的状态为有文件被删除
     */
    private void refreshOneShareStatus(Long shareId) {
        RPanShare entity = getById(shareId);
        if (Objects.isNull(entity)) {
            return;
        }

        ShareStatusEnum shareStatus = ShareStatusEnum.NORMAL;
        if (!checkShareFileAvailable(shareId)) {
            shareStatus = ShareStatusEnum.FILE_DELETED;
        }

        if (Objects.equals(entity.getShareStatus(), shareStatus.getCode())) {
            return;
        }

        doChangeShareStatus(entity, shareStatus);
    }

    /**
     * 执行刷新文件分享状态的动作
     */
    private void doChangeShareStatus(RPanShare entity, ShareStatusEnum shareStatus) {
        entity.setShareStatus(shareStatus.getCode());

        if (!updateById(entity)) {
            applicationContext.publishEvent(new PublishErrorLogEvent(this, "更新分享状态失败，请手动更改状态，分享ID为："
                    + entity.getId() + ", 分享" + "状态改为：" + shareStatus.getCode(), GlobalConst.ZERO_LONG));
        }
    }

    /**
     * 检查该分享所有的文件以及所有的父文件均为正常状态
     */
    private boolean checkShareFileAvailable(Long shareId) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        for (Long fileId : shareFileIdList) {
            if (!checkUpFileAvailable(fileId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查该文件以及所有的文件夹信息均为正常状态
     */
    private boolean checkUpFileAvailable(Long fileId) {
        RPanUserFile entity = iUserFileService.getById(fileId);
        if (Objects.isNull(entity)) {
            return false;
        }
        if (Objects.equals(entity.getDelFlag(), DelFlagEnum.YES.getCode())) {
            return false;
        }
        if (Objects.equals(entity.getParentId(), FileConsts.TOP_PARENT_ID)) {
            return true;
        }
        return checkUpFileAvailable(entity.getParentId());
    }

    /**
     * 通过文件ID查询对应的分享ID集合
     */
    private List<Long> getShareIdListByFileIdList(List<Long> allAvailableFileIdList) {
        LambdaQueryWrapper<RPanShareFile> wrapper = Wrappers.lambdaQuery();
        wrapper.select(RPanShareFile::getShareId);
        wrapper.in(RPanShareFile::getFileId, allAvailableFileIdList);
        return shareFileService.listObjs(wrapper, Long.class::cast);
    }

    public boolean removeById(Serializable id) {
        return cacheService.removeById(id);
    }


    public boolean removeBatchByIds(Collection<?> list) {
        return cacheService.removeBatchByIds(list);
    }

    public boolean updateById(RPanShare entity) {
        return cacheService.updateById(entity.getId(), entity);
    }

    public boolean updateBatchById(Collection<RPanShare> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return true;
        }
        Map<Long, RPanShare> entityMap = entityList.stream()
                .collect(Collectors.toMap(RPanShare::getId, entity -> entity));
        return cacheService.updateByIds(entityMap);
    }

    public RPanShare getById(Serializable id) {
        return cacheService.getById(id);
    }

    public List<RPanShare> listByIds(Collection<? extends Serializable> idList) {
        return cacheService.getByIds(idList);
    }

    // -------------------------------- private --------------------------------


    /**
     * 执行分享文件下载的动作
     * 委托文件模块去做
     */
    private void doDownload(ShareFileDownloadContext context) {
        FileDownloadContext fileDownloadContext = new FileDownloadContext();
        fileDownloadContext.setFileId(context.getFileId());
        fileDownloadContext.setUserId(context.getUserId());
        fileDownloadContext.setResponse(context.getResponse());
        iUserFileService.downloadWithoutCheckUser(fileDownloadContext);
    }

    /**
     * 执行保存我的网盘动作
     * 委托文件模块做文件拷贝的操作
     *
     * @param context
     */
    private void doSaveFiles(ShareSaveContext context) {
        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setFileIdList(context.getFileIdList());
        copyFileContext.setTargetParentId(context.getTargetParentId());
        copyFileContext.setUserId(context.getUserId());
        iUserFileService.copy(copyFileContext);
    }

    /**
     * 校验文件ID是否属于某一个分享
     *
     * @param shareId
     * @param fileIdList
     */
    private void checkFileIdIsOnShareStatus(Long shareId, List<Long> fileIdList) {
        checkFileIdIsOnShareStatusAndGetAllShareUserFiles(shareId, fileIdList);
    }

    /**
     * 校验文件是否处于分享状态，返回该分享的所有文件列表
     *
     * @param shareId
     * @param fileIdList
     * @return
     */
    private List<RPanUserFileVO> checkFileIdIsOnShareStatusAndGetAllShareUserFiles(Long shareId, List<Long> fileIdList) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        if (CollectionUtils.isEmpty(shareFileIdList)) {
            return Lists.newArrayList();
        }
        List<RPanUserFile> allFileRecords = iUserFileService.findAllFileRecordsByFileIdList(shareFileIdList);
        if (CollectionUtils.isEmpty(allFileRecords)) {
            return Lists.newArrayList();
        }
        allFileRecords = allFileRecords.stream()
                .filter(Objects::nonNull)
                .filter(entity -> Objects.equals(entity.getDelFlag(), DelFlagEnum.NO.getCode()))
                .toList();

        List<Long> allFileIdList = allFileRecords.stream().map(RPanUserFile::getId).toList();

        if (allFileIdList.containsAll(fileIdList)) {
            return iUserFileService.transferVOList(allFileRecords);
        }

        throw new RPanBusinessException(ResponseCode.SHARE_FILE_MISS);
    }

    /**
     * 拼装简单文件分享详情的用户信息
     *
     * @param context
     */
    private void assembleShareSimpleUserInfo(QueryShareSimpleDetailContext context) {
        RPanUser entity = userService.getById(context.getEntity().getCreateUser());
        if (entity == null) {
            throw new RPanBusinessException("用户信息查询失败");
        }
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();

        shareUserInfoVO.setUserId(entity.getId());
        shareUserInfoVO.setUsername(encryptUsername(entity.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 填充简单分享详情实体信息
     *
     * @param context
     */
    private void assembleMainShareSimpleInfo(QueryShareSimpleDetailContext context) {
        RPanShare entity = context.getEntity();
        ShareSimpleDetailVO vo = context.getVo();
        vo.setShareId(entity.getId());
        vo.setShareName(entity.getShareName());
    }

    /**
     * 初始化简单分享详情的VO对象
     *
     * @param context
     */
    private void initShareSimpleVO(QueryShareSimpleDetailContext context) {
        ShareSimpleDetailVO vo = new ShareSimpleDetailVO();
        context.setVo(vo);
    }

    /**
     * 查询分享者的信息
     *
     * @param context
     */
    private void assembleShareUserInfo(QueryShareDetailContext context) {
        RPanUser entity = userService.getById(context.getEntity().getCreateUser());
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("用户信息查询失败");
        }
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();

        shareUserInfoVO.setUserId(entity.getId());
        shareUserInfoVO.setUsername(encryptUsername(entity.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 加密用户名称
     *
     * @param username
     * @return
     */
    private String encryptUsername(String username) {
        StringBuilder stringBuffer = new StringBuilder(username);
        stringBuffer.replace(GlobalConst.TWO_INT, username.length() - GlobalConst.TWO_INT, GlobalConst.COMMON_ENCRYPT_STR);
        return stringBuffer.toString();
    }

    /**
     * 查询分享对应的文件列表
     * <p>
     * 1、查询分享对应的文件ID集合
     * 2、根据文件ID来查询文件列表信息
     *
     * @param context
     */
    private void assembleShareFilesInfo(QueryShareDetailContext context) {
        List<Long> fileIdList = getShareFileIdList(context.getShareId());

        QueryFileListContext queryContext = new QueryFileListContext();
        queryContext.setUserId(context.getEntity().getCreateUser());
        queryContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryContext.setFileIdList(fileIdList);

        List<RPanUserFileVO> rPanUserFileVOList = iUserFileService.getFileList(queryContext);
        context.getVo().setUserFileVOList(rPanUserFileVOList);
    }

    /**
     * 查询分享对应的文件ID集合
     *
     * @param shareId
     * @return
     */
    private List<Long> getShareFileIdList(Long shareId) {
        if (shareId == null) {
            throw new RPanBusinessException("分享id不能为空");
        }

        LambdaQueryWrapper<RPanShareFile> wrapper = Wrappers.lambdaQuery();
        wrapper.select(RPanShareFile::getFileId);
        wrapper.eq(RPanShareFile::getShareId, shareId);
        List<Long> sharedFileIds = shareFileService.listObjs(wrapper, Long.class::cast);
        log.info(" ====>>>> shareId = {}, sharedFileIds = {}", shareId, sharedFileIds);
        return sharedFileIds;
    }

    /**
     * 查询分享的主体信息
     *
     * @param context
     */
    private void assembleMainShareInfo(QueryShareDetailContext context) {
        RPanShare entity = context.getEntity();
        ShareDetailVO vo = context.getVo();
        vo.setShareId(entity.getId());
        vo.setShareName(entity.getShareName());
        vo.setShareDay(entity.getShareDay());
        vo.setShareEndTime(entity.getShareEndTime());
    }

    /**
     * 初始化文件详情的VO实体
     *
     * @param context
     */
    private void initShareVO(QueryShareDetailContext context) {
        ShareDetailVO vo = new ShareDetailVO();
        context.setVo(vo);
    }

    /**
     * 生成一个短期的分享token
     *
     * @param context
     * @return
     */
    private String generateShareToken(CheckShareCodeContext context) {
        RPanShare entity = context.getEntity();
        return JwtUtil.generateToken(UUIDUtil.getUUID(), ShareConsts.SHARE_ID, entity.getId(), ShareConsts.ONE_HOUR_LONG);
    }

    /**
     * 校验分享码是不是正确
     *
     * @param context
     */
    private void doCheckShareCode(CheckShareCodeContext context) {
        RPanShare entity = context.getEntity();
        if (!Objects.equals(context.getShareCode(), entity.getShareCode())) {
            throw new RPanBusinessException("分享码错误");
        }
    }

    /**
     * 检查分享的状态是不是正常
     *
     * @param shareId
     * @return
     */
    private RPanShare checkShareStatus(Long shareId) {
        RPanShare entity = getById(shareId);

        if (Objects.isNull(entity)) {
            throw new RPanBusinessException(ResponseCode.SHARE_CANCELLED);
        }

        if (Objects.equals(ShareStatusEnum.FILE_DELETED.getCode(), entity.getShareStatus())) {
            throw new RPanBusinessException(ResponseCode.SHARE_FILE_MISS);
        }

        if (Objects.equals(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode(), entity.getShareDayType())) {
            return entity;
        }

        if (entity.getShareEndTime().isBefore(LocalDateTime.now())) {
            throw new RPanBusinessException(ResponseCode.SHARE_EXPIRE);
        }

        return entity;
    }

    /**
     * 取消文件和分享的关联关系数据
     *
     * @param context
     */
    private void doCancelShareFiles(CancelShareContext context) {
        boolean result = shareFileService.lambdaUpdate()
                .in(RPanShareFile::getShareId, context.getShareIdList())
                .eq(RPanShareFile::getCreateUser, context.getUserId())
                .remove();

        if (!result) {
            throw new RPanBusinessException("取消分享失败");
        }
    }

    /**
     * 执行取消文件分享的动作
     *
     * @param context
     */
    private void doCancelShare(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        if (!removeByIds(shareIdList)) {
            throw new RPanBusinessException("取消分享失败");
        }
    }

    /**
     * 检查用户是否拥有取消对应分享链接的权限
     *
     * @param context
     */
    private void checkUserCancelSharePermission(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        Long userId = context.getUserId();
        List<RPanShare> entitys = listByIds(shareIdList);
        if (CollectionUtils.isEmpty(entitys)) {
            throw new RPanBusinessException("您无权限操作取消分享的动作");
        }
        for (RPanShare ele : entitys) {
            if (!Objects.equals(userId, ele.getCreateUser())) {
                throw new RPanBusinessException("您无权限操作取消分享的动作");
            }
        }
    }

    /**
     * 拼装对应的返回VO
     *
     * @param context
     * @return
     */
    private RPanShareUrlVO assembleShareVO(CreateShareUrlContext context) {
        RPanShare entity = context.getEntity();
        RPanShareUrlVO vo = new RPanShareUrlVO();
        vo.setShareId(entity.getId());
        vo.setShareName(entity.getShareName());
        vo.setShareUrl(entity.getShareUrl());
        vo.setShareCode(entity.getShareCode());
        vo.setShareStatus(entity.getShareStatus());
        return vo;
    }

    /**
     * 保存分享和分享文件的关联关系
     */
    private void saveShareFiles(CreateShareUrlContext context) {
        SaveShareFilesContext saveContext = new SaveShareFilesContext();
        saveContext.setShareId(context.getEntity().getId());
        saveContext.setShareFileIdList(context.getShareFileIdList());
        saveContext.setUserId(context.getUserId());
        shareFileService.saveShareFiles(saveContext);
    }

    /**
     * 拼装分享的实体，并保存到数据库中
     *
     * @param context
     */
    private void saveShare(CreateShareUrlContext context) {
        RPanShare entity = new RPanShare();

        entity.setShareName(context.getShareName());
        entity.setShareType(context.getShareType());
        entity.setShareDayType(context.getShareDayType());

        Integer shareDay = ShareDayTypeEnum.getShareDayByCode(context.getShareDayType());
        if (Objects.equals(GlobalConst.MINUS_ONE_INT, shareDay)) {
            throw new RPanBusinessException("分享天数非法");
        }

        entity.setShareDay(shareDay);
        LocalDateTime now = LocalDateTime.now();
        entity.setShareEndTime(now.plusDays(shareDay));
        entity.setShareCode(createShareCode());
        entity.setShareStatus(ShareStatusEnum.NORMAL.getCode());
        entity.setCreateUser(context.getUserId());

        if (!save(entity)) {
            throw new RPanBusinessException("保存分享信息失败");
        }

        String shareUrl = this.createShareUrl(entity.getId());
        entity.setShareUrl(shareUrl);
        updateById(entity);

        context.setEntity(entity);
    }

    /**
     * 创建分享的分享码
     */
    private String createShareCode() {
        return RandomStringUtils.randomAlphabetic(4).toLowerCase();
    }

    /**
     * 创建分享的URL
     */
    private String createShareUrl(Long shareId) {
        if (shareId == null) {
            throw new RPanBusinessException("分享的ID为空");
        }
        String sharePrefix = config.getSharePrefix();
        if (!sharePrefix.endsWith(GlobalConst.SLASH_STR)) {
            sharePrefix += GlobalConst.SLASH_STR;
        }
        return sharePrefix + URLEncoder.encode(IdUtil.encrypt(shareId), StandardCharsets.UTF_8);
    }

}




