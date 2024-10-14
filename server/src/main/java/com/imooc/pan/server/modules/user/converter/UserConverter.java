package com.imooc.pan.server.modules.user.converter;

import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.server.modules.user.context.*;
import com.imooc.pan.server.modules.user.entity.RPanUser;
import com.imooc.pan.server.modules.user.po.*;
import com.imooc.pan.server.modules.user.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户模块实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    UserRegisterContext convertPO2Context(UserRegisterPO userRegisterPO);

    @Mapping(target = "password", ignore = true)
    RPanUser userRegisterContext2RPanUser(UserRegisterContext userRegisterContext);

    UserLoginContext convertPO2Context(UserLoginPO userLoginPO);

    @Mapping(target = "password", ignore = true)
    RPanUser userLoginContext2RPanUser(UserLoginContext userLoginContext);

    CheckUsernameContext convertPO2Context(CheckUsernamePO checkUsernamePO);

    CheckAnswerContext convertPO2Context(CheckAnswerPO checkAnswerPO);

    ResetPasswordContext convertPO2Context(ResetPasswordPO resetPasswordPO);

    ChangePasswordContext convertPO2Context(ChangePasswordPO changePasswordPO);

    /**
     * 拼装用户基本信息返回实体
     */
    @Mapping(source = "entity.id", target = "rootFileId")
    @Mapping(source = "entity.filename", target = "rootFileName")
    UserInfoVO assembleUserInfoVO(RPanUser rPanUser, RPanUserFile entity);

    UserLoginByGithubContext convertPO2Context(UserLoginByGithubPO po);

}
