package com.imooc.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.response.ResponseCode;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.core.utils.PasswordUtil;
import com.imooc.pan.server.modules.file.constants.FileConstants;
import com.imooc.pan.server.modules.file.context.CreateFolderContext;
import com.imooc.pan.server.modules.file.service.IUserFileService;
import com.imooc.pan.server.modules.user.context.UserRegisterContext;
import com.imooc.pan.server.modules.user.converter.UserConverter;
import com.imooc.pan.server.modules.user.entity.RPanUser;
import com.imooc.pan.server.modules.user.mapper.RPanUserMapper;
import com.imooc.pan.server.modules.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 18063
 * @description 针对表【r_pan_user(用户信息表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:06:46
 */
@Service
public class UserServiceImpl extends ServiceImpl<RPanUserMapper, RPanUser> implements IUserService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private IUserFileService iUserFileService;

    /**
     * 用户注册的业务实现
     * 需要实现的功能点
     * 1. 注册用户信息
     * 2. 创建新用户的根目录的信息
     * <p>
     * 需要实现的技术难点:
     * 1. 该业务是幂等的
     * 2. 要保证用户名全局唯一
     * <p>
     * 处理方案:
     * 幂等性通过数据库表对于用户名字段添加唯一索引, 上游业务捕获对应的冲突异常, 转化返回
     *
     * @param userRegisterContext
     * @return
     */
    @Override
    public Long register(UserRegisterContext userRegisterContext) {

        assembleUserEntity(userRegisterContext);

        doRegister(userRegisterContext);
        createUserRootFolder(userRegisterContext);

        return userRegisterContext.getEntity().getUserId();
    }

    /**
     * 实体转化, 由上下文信息转换成用户实体, 封装进上下文
     *
     * @param userRegisterContext
     * @return
     */
    private RPanUser assembleUserEntity(UserRegisterContext userRegisterContext) {
        RPanUser entity = userConverter.userRegisterContext2RPanUser(userRegisterContext);
        String salt = PasswordUtil.getSalt();

        entity.setUserId(IdUtil.get());
        entity.setSalt(salt);
        String dbPassword = PasswordUtil.encryptPassword(salt, userRegisterContext.getPassword());
        entity.setPassword(dbPassword);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        userRegisterContext.setEntity(entity);

        return entity;
    }

    /**
     * 实现用户注册的业务
     * 需要捕获数据库的唯一索引冲突异常, 来实现全局用户名称唯一的保证
     *
     * @param userRegisterContext
     */
    private void doRegister(UserRegisterContext userRegisterContext) {
        RPanUser entity = userRegisterContext.getEntity();
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
     * 创建用户的根目录信息
     *
     * @param userRegisterContext
     */
    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        createFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);

        Long folder = iUserFileService.createFolder(createFolderContext);

    }

}




