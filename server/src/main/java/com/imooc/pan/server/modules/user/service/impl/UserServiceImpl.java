package com.imooc.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.cache.core.constants.CacheConstants;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.response.ResponseCode;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.core.utils.JwtUtil;
import com.imooc.pan.core.utils.PasswordUtil;
import com.imooc.pan.server.common.cache.AnnotationCacheService;
import com.imooc.pan.server.modules.file.constants.FileConsts;
import com.imooc.pan.server.modules.file.context.CreateFolderContext;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.server.modules.file.service.IUserFileService;
import com.imooc.pan.server.modules.user.constants.UserConstants;
import com.imooc.pan.server.modules.user.context.*;
import com.imooc.pan.server.modules.user.converter.UserConverter;
import com.imooc.pan.server.modules.user.entity.RPanThirdPartyAuth;
import com.imooc.pan.server.modules.user.entity.RPanUser;
import com.imooc.pan.server.modules.user.enums.ThirdPartyProviderEnum;
import com.imooc.pan.server.modules.user.mapper.RPanUserMapper;
import com.imooc.pan.server.modules.user.service.GithubService;
import com.imooc.pan.server.modules.user.service.IThirdPartyAuthService;
import com.imooc.pan.server.modules.user.service.IUserService;
import com.imooc.pan.server.modules.user.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 18063
 * @description 针对表【r_pan_user(用户信息表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:06:46
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<RPanUserMapper, RPanUser> implements IUserService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private GithubService githubService;

    @Autowired
    private IThirdPartyAuthService iThirdPartyAuthService;

    @Autowired
    @Qualifier(value = "userAnnotationCacheService")
    private AnnotationCacheService<RPanUser> cacheService;
    @Autowired
    private ThirdPartyAuthServiceImpl thirdPartyAuthServiceImpl;

    /**
     * 用户注册的业务方法实现
     * 1. 注册用户信息, 保证用户名的唯一性, 并保存入库
     * 2. 创建用户的根目录信息
     *
     * @param userRegisterContext 用户注册上下文对象, 包含注册所需的字段
     * @return 注册成功的用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterContext userRegisterContext) {
        RPanUser entity = this.assembleUserEntity(userRegisterContext);
        this.doRegister(entity);
        this.createUserRootFolder(userRegisterContext);
        return userRegisterContext.getEntity().getUserId();
    }

    /**
     * 用户登录业务实现
     * 1. 校验信息
     * 2. 生成一个具有时效性的认证令牌
     * 3. 缓存令牌信息, 实现单机登录
     *
     * @param userLoginContext 用户登录上下文对象
     * @return 访问令牌
     */
    @Override
    public String login(UserLoginContext userLoginContext) {
        checkLoginInfo(userLoginContext);
        generateAndSaveAccessToken(userLoginContext);
        return userLoginContext.getAccessToken();
    }

    /**
     * github授权登录
     * <p>
     * 1. 调用github接口获取用户访问令牌和个人信息, 如果失败就直接抛错
     * 2. 如果用户尚未注册账号, 就自动进行注册
     * 3. 执行登录动作, 返回令牌
     *
     * @param context
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String loginByGithub(UserLoginByGithubContext context) {
        String code = context.getCode();
        if (StringUtils.isBlank(code)) {
            throw new RPanBusinessException("code为空");
        }

        String githubAccessToken = Optional.ofNullable(githubService.getAccessToken(code))
                .orElseThrow(() -> new RPanBusinessException("获取github访问令牌失败"));
        Map<String, Object> githubUserInfo = Optional.ofNullable(githubService.getUserInfo(githubAccessToken))
                .orElseThrow(() -> new RPanBusinessException("获取github用户信息失败"));
        log.info("githubAccessToken = {}", githubAccessToken);
        log.info("githubUserInfo = {}", githubUserInfo);
        String githubUsername = String.valueOf(githubUserInfo.get("login"));
        String githubUid = String.valueOf(githubUserInfo.get("id"));

        // -------------------------------- 通过 渠道名+uid 判断用户是否已存在, 否则自动注册 --------------------------------
        RPanThirdPartyAuth certificationRecord = this.iThirdPartyAuthService.lambdaQuery()
                .eq(RPanThirdPartyAuth::getProvider, ThirdPartyProviderEnum.GITHUB.getProvider())
                .eq(RPanThirdPartyAuth::getProviderUid, githubUid)
                .one();

        Long userId;

        // 如果没有第三方认证记录, 表示当前是第1次授权登录, 自动注册账号, 关联第三方, 并执行登录...
        if (certificationRecord == null) {
            UserRegisterContext registerContext = new UserRegisterContext();
            registerContext.setUsername(githubUsername);
            registerContext.setPassword("");
            registerContext.setQuestion("");
            registerContext.setAnswer("");
            userId = this.register(registerContext);

            // 插入一条第三方认证记录
            RPanThirdPartyAuth authRecord = new RPanThirdPartyAuth();
            authRecord.setProvider(ThirdPartyProviderEnum.GITHUB.getProvider());
            authRecord.setProviderUid(githubUid);
            authRecord.setUserId(userId);
            authRecord.setCreateTime(LocalDateTime.now());
            thirdPartyAuthServiceImpl.save(authRecord);
        } else {
            // 如果有第三方认证记录, 则直接通过此记录找到关联的用户, 并执行登录...
            userId = certificationRecord.getUserId();
            Optional.ofNullable(this.getById(userId)).orElseThrow(() -> new RPanBusinessException(ResponseCode.USER_NOT_EXISTS));
        }

        // 执行登录动作, 返回令牌
        UserLoginContext loginContext = new UserLoginContext();
        loginContext.setEntity(getById(userId));
        generateAndSaveAccessToken(loginContext);
        return loginContext.getAccessToken();
    }

    /**
     * 用户退出登录
     * 清除用户的登录凭证缓存
     *
     * @param userId 用户id
     */
    @Override
    public void exit(Long userId) {
        try {
            Cache cache = cacheManager.getCache(CacheConstants.R_PAN_CACHE_NAME);
            cache.evict(UserConstants.USER_LOGIN_PREFIX + userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPanBusinessException("退出登录失败");
        }
    }

    /**
     * 校验用户名
     */
    @Override
    public String checkUsername(CheckUsernameContext checkUsernameContext) {
        LambdaQueryWrapper<RPanUser> wrapper = Wrappers.<RPanUser>lambdaQuery()
                .eq(RPanUser::getUsername, checkUsernameContext.getUsername());
        String question = Optional.ofNullable(this.baseMapper.selectOne(wrapper))
                .map(RPanUser::getQuestion).orElse(null);

        if (StringUtils.isBlank(question)) {
            throw new RPanBusinessException("用户不存在");
        }

        return question;
    }

    @Override
    public String checkAnswer(CheckAnswerContext checkAnswerContext) {

        LambdaQueryWrapper<RPanUser> wrapper = Wrappers.<RPanUser>lambdaQuery()
                .eq(RPanUser::getUsername, checkAnswerContext.getUsername())
                .eq(RPanUser::getQuestion, checkAnswerContext.getQuestion())
                .eq(RPanUser::getAnswer, checkAnswerContext.getAnswer());

        long count = this.baseMapper.selectCount(wrapper);

        if (count == 0L) {
            throw new RPanBusinessException("密保验证失败");
        }

        return generateCheckAccessToken(checkAnswerContext);
    }

    /**
     * 重置用户密码
     * 1. 校验token是否有效
     * 2. 重置密码
     *
     * @param context
     */
    @Override
    public void resetPassword(ResetPasswordContext context) {
        this.checkForgetPwdToken(context);
        this.checkAndResetPwd(context);
    }

    /**
     * 在线修改密码
     * 1. 校验旧密码
     * 2. 重置新密码
     * 3. 退出当前的登录状态
     *
     * @param context
     */
    @Override
    public void changePassword(ChangePasswordContext context) {
        this.checkOldPassword(context);
        this.doChangePassword(context);
        this.exit(context.getUserId());
    }

    /**
     * 查询当前登录用户基本信息
     * 1. 查询用户的基本信息实体
     * 2. 查询用户的跟文件夹信息
     * 3. 拼装VO对象返回
     *
     * @param userId 用户id
     * @return
     */
    @Override
    public UserInfoVO info(Long userId) {
        RPanUser entity = Optional.ofNullable(this.getById(userId))
                .orElseThrow(() -> new RPanBusinessException("用户不存在"));

        RPanUserFile rootFile = Optional.ofNullable(this.iUserFileService.getUserRootFile(userId))
                .orElseThrow(() -> new RPanBusinessException("用户根目录不存在"));

        return userConverter.assembleUserInfoVO(entity, rootFile);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list) {
        throw new RPanBusinessException("请更换手动缓存");
    }

    @Override
    public boolean updateById(RPanUser entity) {
        return cacheService.updateById(entity.getUserId(), entity);
    }

    @Override
    public boolean removeById(Serializable id) {
        return cacheService.removeById(id);
    }

    @Override
    public boolean updateBatchById(Collection<RPanUser> entityList) {
        throw new RPanBusinessException("请更换手动缓存");
        // return super.updateBatchById(entityList);
    }

    @Override
    public RPanUser getById(Serializable id) {
        return cacheService.getById(id);
    }

    @Override
    public List<RPanUser> listByIds(Collection<? extends Serializable> idList) {
        throw new RPanBusinessException("请更换手动缓存");
        // return super.listByIds(idList);
    }

    // ******************************** private ********************************

    /**
     * 修改新密码
     *
     * @param context
     */
    private void doChangePassword(ChangePasswordContext context) {
        String newPassword = context.getNewPassword();
        RPanUser entity = context.getEntity();
        String salt = entity.getSalt();

        String encNewPwd = PasswordUtil.encryptPassword(salt, newPassword);
        entity.setPassword(encNewPwd);
        if (!this.updateById(entity)) {
            throw new RPanBusinessException("用户密码修改失败");
        }
    }

    /**
     * 校验用户旧密码
     * 该步骤会封装用户的实体信息到上下文对象中
     *
     * @param context
     */
    private void checkOldPassword(ChangePasswordContext context) {
        Long userId = context.getUserId();
        String oldPassword = context.getOldPassword();
        RPanUser entity = Optional.ofNullable(this.getById(userId))
                .orElseThrow(() -> new RPanBusinessException("用户不存在"));

        context.setEntity(entity);
        String encOldPwd = PasswordUtil.encryptPassword(entity.getSalt(), oldPassword);
        String dbPassword = entity.getPassword();
        if (!Objects.equals(encOldPwd, dbPassword)) {
            throw new RPanBusinessException("旧密码不正确");
        }
    }

    /**
     * 校验用户信息并重置用户密码
     *
     * @param context
     */
    private void checkAndResetPwd(ResetPasswordContext context) {
        String username = context.getUsername();
        String password = context.getPassword();
        RPanUser entity = Optional.ofNullable(this.getRPanUserByUsername(username))
                .orElseThrow(() -> new RPanBusinessException("用户不存在"));

        String dbPwd = PasswordUtil.encryptPassword(entity.getSalt(), password);
        entity.setPassword(dbPwd);
        entity.setUpdateTime(LocalDateTime.now());
        if (!this.updateById(entity)) {
            throw new RPanBusinessException("重置密码失败");
        }
    }

    /**
     * 验证当前token是否合法
     * 此token为回答密保问题后生成的临时token
     *
     * @param context
     */
    private void checkForgetPwdToken(ResetPasswordContext context) {
        String token = context.getToken();
        Object value = JwtUtil.analyzeToken(token, UserConstants.FORGET_USERNAME);
        if (value == null) {
            throw new RPanBusinessException(ResponseCode.TOKEN_EXPIRE);
        }
        String tokenUsername = String.valueOf(value);
        if (!Objects.equals(tokenUsername, context.getUsername())) {
            throw new RPanBusinessException("token非法");
        }
    }

    /**
     * 生成用户忘记密码-校验密保答案通过的临时token
     * token的有效时间为5分钟
     *
     * @param checkAnswerContext
     * @return
     */
    private String generateCheckAccessToken(CheckAnswerContext checkAnswerContext) {
        return JwtUtil.generateToken(checkAnswerContext.getUsername(), UserConstants.FORGET_USERNAME,
                checkAnswerContext.getUsername(), UserConstants.FIVE_MINUTES_LONG);
    }

    private void generateAndSaveAccessToken(UserLoginContext userLoginContext) {
        RPanUser entity = userLoginContext.getEntity();

        String accessToken = JwtUtil.generateToken(entity.getUsername(), UserConstants.LOGIN_USER_ID,
                entity.getUserId(), UserConstants.ONE_DAY_LONG);

        Cache cache = this.cacheManager.getCache(CacheConstants.R_PAN_CACHE_NAME);
        cache.put(UserConstants.USER_LOGIN_PREFIX + entity.getUserId(), accessToken);

        userLoginContext.setAccessToken(accessToken);
    }

    /**
     * 校验用户名密码
     *
     * @param userLoginContext
     */
    private void checkLoginInfo(UserLoginContext userLoginContext) {
        String username = userLoginContext.getUsername();
        String password = userLoginContext.getPassword();

        RPanUser entity = this.getRPanUserByUsername(username);
        if (entity == null) {
            throw new RPanBusinessException("用户名或密码错误");
        }

        String salt = entity.getSalt();
        String encPassword = PasswordUtil.encryptPassword(salt, password);
        String dbPassword = entity.getPassword();
        if (!Objects.equals(dbPassword, encPassword)) {
            throw new RPanBusinessException("用户名或密码错误");
        }
        userLoginContext.setEntity(entity);
    }

    private RPanUser getRPanUserByUsername(String username) {
        return getOne(Wrappers.<RPanUser>lambdaQuery().eq(RPanUser::getUsername, username));
    }

    /**
     * 工具方法
     * 将上下文对象的属性值集成为entity, 并封装进上下文对象的属性中
     *
     * @param context 上下文对象
     * @return 已经集成的上下文对象
     */
    private RPanUser assembleUserEntity(UserRegisterContext context) {
        RPanUser entity = userConverter.userRegisterContext2RPanUser(context);
        String salt = PasswordUtil.getSalt();

        entity.setUserId(IdUtil.get());
        entity.setSalt(salt);
        String dbPassword = PasswordUtil.encryptPassword(salt, context.getPassword());
        entity.setPassword(dbPassword);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        context.setEntity(entity);

        return entity;
    }

    /**
     * 将entity对象存入数据库
     * 对象表的用户名字段有唯一索引, 针对性捕获 DuplicateKeyException 进行处理, 并提示 '用户名已存在'
     *
     * @param entity
     */
    private void doRegister(RPanUser entity) {
        if (entity == null) {
            throw new RPanBusinessException(ResponseCode.ERROR);
        }

        try {
            if (!save(entity)) {
                throw new RPanBusinessException("用户注册失败");
            }
        } catch (DuplicateKeyException e) {
            throw new RPanBusinessException("用户名已存在");
        }

    }

    /**
     * 创建用户的云盘根目录
     * 将各个字段封装好后, 移交给文件模块的业务层进行处理
     * 用户根目录的父目录id为0; 所属人id为当前用户id; 文件夹名称为'全部文件'
     *
     * @param userRegisterContext 上下文对象
     */
    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConsts.TOP_PARENT_ID);
        createFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        createFolderContext.setFolderName(FileConsts.ALL_FILE_CN_STR);

        iUserFileService.createFolder(createFolderContext);
    }

}




