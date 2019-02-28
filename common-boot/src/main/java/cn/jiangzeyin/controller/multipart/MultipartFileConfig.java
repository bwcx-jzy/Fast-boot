package cn.jiangzeyin.controller.multipart;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.UserInfo;

/**
 * 上传文件保存路径
 *
 * @author jiangzeyin
 * @date 2017/10/25
 */
public class MultipartFileConfig {

    private static String fileTempPath;
    private static final UserInfo USER_INFO = new UserInfo();

    /**
     * 设置文件上传保存路径
     *
     * @param fileTempPath path
     */
    public static void setFileTempPath(String fileTempPath) {
        MultipartFileConfig.fileTempPath = fileTempPath;
    }

    public static String getFileTempPath() {
        if (StrUtil.isBlank(fileTempPath)) {
            fileTempPath = USER_INFO.getTempDir();
        }
        return fileTempPath;
    }
}
