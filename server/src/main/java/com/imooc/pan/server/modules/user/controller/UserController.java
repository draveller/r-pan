package com.imooc.pan.server.modules.user.controller;

import com.imooc.pan.core.response.R;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.server.common.annotation.LoginIgnore;
import com.imooc.pan.server.common.utils.UserIdUtil;
import com.imooc.pan.server.modules.user.context.*;
import com.imooc.pan.server.modules.user.converter.UserConverter;
import com.imooc.pan.server.modules.user.po.*;
import com.imooc.pan.server.modules.user.service.IUserService;
import com.imooc.pan.server.modules.user.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 该类是用户模块的控制器类
 */
@RestController
@RequestMapping("/user")
@Tag("用户模块")
public class UserController {

    @Resource
    private IUserService iUserService;

    @Resource
    private UserConverter userConverter;

    @LoginIgnore
    @Operation(
            summary = "用户注册接口",
            description = "该接口提供了用户注册功能, 实现了幂等性注册的逻辑, 可多并发调用"
    )
    @PostMapping("/register")
    public R<Object> register(@Validated @RequestBody UserRegisterPO userRegisterPO) {
        UserRegisterContext userRegisterContext = this.userConverter.convertPO2Context(userRegisterPO);

        Long userId = this.iUserService.register(userRegisterContext);

        return R.data(IdUtil.encrypt(userId));
    }

    @LoginIgnore
    @Operation(
            summary = "用户登录接口",
            description = "该接口提供了用户登录功能, 成功登录之后会返回有时效性的accessToken, 供后续服务使用"
    )
    @PostMapping("/login")
    public R<Object> login(@Validated @RequestBody UserLoginPO userLoginPO) {
        UserLoginContext userLoginContext = this.userConverter.convertPO2Context(userLoginPO);

        String accessToken = this.iUserService.login(userLoginContext);
        return R.data(accessToken);
    }

    @LoginIgnore
    @Operation(
            summary = "用户github登录接口",
            description = "该接口提供了用户登录功能, 成功登录之后会返回有时效性的accessToken, 供后续服务使用"
    )
    @PostMapping("/login-by-github")
    public R<Object> loginByGithub(@Validated @RequestBody UserLoginByGithubPO po) {
        UserLoginByGithubContext context = this.userConverter.convertPO2Context(po);
        String accessToken = this.iUserService.loginByGithub(context);
        return R.data(accessToken);
    }

    @Operation(
            summary = "用户登出接口",
            description = "该接口提供了用户登出功能"
    )
    @PostMapping("/exit")
    public R<Object> exit() {
        this.iUserService.exit(UserIdUtil.get());
        return R.success();
    }

    @LoginIgnore
    @Operation(
            summary = "用户忘记密码-校验用户名接口",
            description = "该接口提供了用户忘记密码-校验用户名功能, 返回用户设置的密保问题"
    )
    @PostMapping("/username/check")
    public R<Object> checkUsername(@Validated @RequestBody CheckUsernamePO checkUsernamePO) {
        CheckUsernameContext checkUsernameContext = this.userConverter.convertPO2Context(checkUsernamePO);
        String question = this.iUserService.checkUsername(checkUsernameContext);
        return R.data(question);
    }

    @LoginIgnore
    @Operation(
            summary = "用户忘记密码-校验密保答案",
            description = "该接口提供了用户忘记密码-校验密保答案功能, 校验成功后返回token"
    )
    @PostMapping("/answer/check")
    public R<Object> checkAnswer(@Validated @RequestBody CheckAnswerPO checkAnswerPO) {
        CheckAnswerContext checkAnswerContext = this.userConverter.convertPO2Context(checkAnswerPO);
        String token = this.iUserService.checkAnswer(checkAnswerContext);
        return R.data(token);
    }

    @LoginIgnore
    @Operation(
            summary = "用户忘记密码-重置密码",
            description = "该接口提供了用户忘记密码-重置密码功能"
    )
    @PostMapping("/password/reset")
    public R<Object> resetPassword(@Validated @RequestBody ResetPasswordPO resetPasswordPO) {
        ResetPasswordContext resetPasswordContext = this.userConverter.convertPO2Context(resetPasswordPO);
        this.iUserService.resetPassword(resetPasswordContext);
        return R.success();
    }

    @Operation(
            summary = "用户在线修改密码",
            description = "该接口提供了用户修改密码功能"
    )
    @PostMapping("/password/change")
    public R<Object> changePassword(@Validated @RequestBody ChangePasswordPO changePasswordPO) {
        ChangePasswordContext context = this.userConverter.convertPO2Context(changePasswordPO);
        this.iUserService.changePassword(context);
        return R.success();

    }

    @Operation(
            summary = "查询登录用户信息",
            description = "该接口提供了查询登录用户信息功能"
    )
    @GetMapping("/")
    public R<UserInfoVO> info() {
        UserInfoVO userInfoVO = this.iUserService.info(UserIdUtil.get());
        return R.data(userInfoVO);
    }


}
