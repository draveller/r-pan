package com.imooc.pan.server.modules.user.context;

import com.imooc.pan.server.modules.user.entity.RPanUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户校验密保答案的实体对象
 */
@Data
public class CheckAnswerContext implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;

    /**
     * 用户实体对象
     */
    private RPanUser entity;

}
