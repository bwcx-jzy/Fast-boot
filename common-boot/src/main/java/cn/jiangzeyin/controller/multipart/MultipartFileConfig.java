package cn.jiangzeyin.controller.multipart;

import cn.hutool.core.util.StrUtil;

/**
 * 上传文件保存路径
 *
 * @author jiangzeyin
 * data 2017/10/25
 */
public class MultipartFileConfig {
    private static String fileTempPath;

    public static void setFileTempPath(String fileTempPath) {
        MultipartFileConfig.fileTempPath = fileTempPath;
    }

    public static String getFileTempPath() {
        if (StrUtil.isBlank(fileTempPath)) {
            throw new IllegalArgumentException("please set  fileTempPath");
        }
        return fileTempPath;
    }
}
