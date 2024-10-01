package com.imooc.pan.server.modules.user.controller;

import com.imooc.pan.core.response.R;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.server.common.utils.UserIdUtil;
import com.imooc.pan.server.modules.user.context.UserLoginContext;
import com.imooc.pan.server.modules.user.context.UserRegisterContext;
import com.imooc.pan.server.modules.user.converter.UserConverter;
import com.imooc.pan.server.modules.user.po.UserLoginPO;
import com.imooc.pan.server.modules.user.po.UserRegisterPO;
import com.imooc.pan.server.modules.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 该类是用户模块的控制器类
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户模块")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private UserConverter userConverter;

    @ApiOperation(
            value = "用户注册接口",
            notes = "该接口提供了用户注册功能, 实现了幂等性注册的逻辑, 可多并发调用",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PatchMapping("register")
    public R<Object> register(@Validated @RequestBody UserRegisterPO userRegisterPO) {
        UserRegisterContext userRegisterContext = userConverter.userRegisterPO2UserRegisterContext(userRegisterPO);

        Long userId = iUserService.register(userRegisterContext);

        return R.data(IdUtil.encrypt(userId));
    }

    @ApiOperation(
            value = "用户登录接口",
            notes = "该接口提供了用户登录功能, 成功登录之后会返回有时效性的accessToken, 供后续服务使用",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PatchMapping("login")
    public R<Object> login(@Validated @RequestBody UserLoginPO userLoginPO) {
        UserLoginContext userLoginContext = userConverter.userLoginPO2UserLoginContext(userLoginPO);

        String accessToken = iUserService.login(userLoginContext);
        return R.data(accessToken);
    }

    @ApiOperation(
            value = "用户登出接口",
            notes = "该接口提供了用户登出功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PatchMapping("exit")
    public R<Object> exit() {
        iUserService.exit(UserIdUtil.get());
        return R.success();
    }

}
