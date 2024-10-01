package com.imooc.pan.server.modules.user.converter;

import com.imooc.pan.server.modules.user.context.UserLoginContext;
import com.imooc.pan.server.modules.user.context.UserRegisterContext;
import com.imooc.pan.server.modules.user.entity.RPanUser;
import com.imooc.pan.server.modules.user.po.UserLoginPO;
import com.imooc.pan.server.modules.user.po.UserRegisterPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户模块实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * 将用户注册的PO对象转化为用户注册的context对象
     *
     * @param userRegisterPO
     * @return
     */
    UserRegisterContext userRegisterPO2UserRegisterContext(UserRegisterPO userRegisterPO);

    @Mapping(target = "password", ignore = true)
    RPanUser userRegisterContext2RPanUser(UserRegisterContext userRegisterContext);

    UserLoginContext userLoginPO2UserLoginContext(UserLoginPO userLoginPO);

    @Mapping(target = "password", ignore = true)
    RPanUser userLoginContext2RPanUser(UserLoginContext userLoginContext);

}
