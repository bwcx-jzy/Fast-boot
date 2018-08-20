package cn.jiangzeyin.util;

import cn.hutool.core.util.StrUtil;

/**
 * Created by jiangzeyin on 2018/8/20.
 *
 * @author jiangzeyin
 */
public class StringUtil {
    /**
     * 简化class name
     *
     * @param className className
     * @return 结果
     */
    public static String simplifyClassName(String className) {
        String[] packages = StrUtil.split(className, ".");
        if (packages == null || packages.length < 1) {
            return className;
        }
        int len = packages.length;
        if (len == 1) {
            return packages[0];
        }
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < len - 1; i++) {
            String item = packages[i];
            name.append(item, 0, 1).append(".");
        }
        name.append(packages[len - 1]);
        return name.toString();
    }
}
