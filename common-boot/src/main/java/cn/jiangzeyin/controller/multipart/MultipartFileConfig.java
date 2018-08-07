package cn.jiangzeyin.controller.multipart;

import cn.jiangzeyin.StringUtil;

/**
 * 上传文件保存路径
 *
 * @author jiangzeyin
 * @date 2017/10/25
 */
public class MultipartFileConfig {
    private static String fileTempPath;

    public static void setFileTempPath(String fileTempPath) {
        MultipartFileConfig.fileTempPath = fileTempPath;
    }

    public static String getFileTempPath() {
        if (StringUtil.isEmpty(fileTempPath)) {
            throw new IllegalArgumentException("please set  fileTempPath");
        }
        return fileTempPath;
    }
}
