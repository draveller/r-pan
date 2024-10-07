package com.imooc.pan.server.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pan.server.modules.user.context.*;
import com.imooc.pan.server.modules.user.entity.RPanUser;
import com.imooc.pan.server.modules.user.vo.UserInfoVO;

/**
 * @author 18063
 * @description 针对表【r_pan_user(用户信息表)】的数据库操作Service
 * @createDate 2024-09-28 14:06:46
 */
public interface IUserService extends IService<RPanUser> {

    Long register(UserRegisterContext userRegisterContext);

    String login(UserLoginContext userLoginContext);

    String loginByGithub(UserLoginByGithubContext context);

    void exit(Long aLong);

    String checkUsername(CheckUsernameContext checkUsernameContext);

    String checkAnswer(CheckAnswerContext checkAnswerContext);

    void resetPassword(ResetPasswordContext resetPasswordContext);

    void changePassword(ChangePasswordContext context);

    UserInfoVO info(Long userId);

}
