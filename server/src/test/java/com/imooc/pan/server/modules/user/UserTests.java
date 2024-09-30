package com.imooc.pan.server.modules.user;

import cn.hutool.core.lang.Assert;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.server.RPanServerLauncher;
import com.imooc.pan.server.modules.user.context.UserRegisterContext;
import com.imooc.pan.server.modules.user.service.IUserService;
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
        UserRegisterContext context = getUserRegisterContext();
        iUserService.register(context);
        iUserService.register(context);
    }


    private UserRegisterContext getUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername("张三");
        context.setPassword("12345678");
        context.setQuestion("你叫什么名字");
        context.setAnswer("小张");
        return context;
    }
}
