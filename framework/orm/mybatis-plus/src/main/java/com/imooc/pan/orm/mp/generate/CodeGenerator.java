package com.imooc.pan.orm.mp.generate;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

/**
 * 具体实现略, 参见mp官网..
 * mp代码生成工具类
 * 可以选择安装MyBatisX插件生成
 * 或者执行该类工具方法生成
 */
public class CodeGenerator {

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help);
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void generate() {
    }

}
