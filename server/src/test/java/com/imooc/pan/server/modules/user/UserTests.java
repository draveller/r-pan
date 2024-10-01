package com.imooc.pan.server.modules.user;

import cn.hutool.core.lang.Assert;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.utils.JwtUtil;
import com.imooc.pan.server.RPanServerLauncher;
import com.imooc.pan.server.modules.user.constants.UserConstants;
import com.imooc.pan.server.modules.user.context.UserLoginContext;
import com.imooc.pan.server.modules.user.context.UserRegisterContext;
import com.imooc.pan.server.modules.user.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户模块单元测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RPanServerLauncher.class)
@Transactional
public class UserTests {

    @Autowired
    private IUserService iUserService;

    /**
     * 测试成功注册用户信息
     */
    @Test
    public void test_register_user() {
        UserRegisterContext context = getUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register > 0L);
    }

    /**
     * 测试重复用户名称注册幂等
     */
    @Test(expected = RPanBusinessException.class)
    public void test_register_duplicate_username() {
        UserRegisterContext context = this.getUserRegisterContext();
        iUserService.register(context);
        iUserService.register(context);
    }

    /**
     * 测试登录成功
     */
    @Test
    public void login_success() {
        UserRegisterContext context = getUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register > 0L);

        UserLoginContext userLoginContext = this.getUserLoginContext();
        String accessToken = iUserService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNotBlank(accessToken));
    }

    /**
     * 测试用户名错误, 登录失败
     */
    @Test(expected = RPanBusinessException.class)
    public void wrong_username() {
        UserRegisterContext context = getUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register > 0L);

        UserLoginContext userLoginContext = this.getUserLoginContext();
        userLoginContext.setUsername("李四-John-王天霸");
        String accessToken = iUserService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNotBlank(accessToken));

    }

    /**
     * 测试密码错误, 登录失败
     */
    @Test(expected = RPanBusinessException.class)
    public void wrong_password() {
        UserRegisterContext context = getUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register > 0L);

        UserLoginContext userLoginContext = this.getUserLoginContext();
        userLoginContext.setPassword("xxxxxxxx");
        String accessToken = iUserService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNotBlank(accessToken));

    }

    @Test
    public void test_exit() {
        UserRegisterContext context = getUserRegisterContext();
        Long register = iUserService.register(context);
        Assert.isTrue(register > 0L);

        UserLoginContext userLoginContext = this.getUserLoginContext();
        String accessToken = iUserService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNotBlank(accessToken));

        Long userId = (Long)JwtUtil.analyzeToken(accessToken, UserConstants.LOGIN_USER_ID);
        iUserService.exit(userId);
    }

    // ******************************** private ********************************

    private final static String USERNAME = "张三";
    private final static String PASSWORD = "12345678";

    private UserLoginContext getUserLoginContext() {
        UserLoginContext userLoginContext = new UserLoginContext();
        userLoginContext.setUsername(USERNAME);
        userLoginContext.setPassword(PASSWORD);
        return userLoginContext;
    }

    private UserRegisterContext getUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion("你叫什么名字");
        context.setAnswer("张霸天");
        return context;
    }

}
