package cn.jiangzeyin.controller.multipart;

/**
 * Created by jiangzeyin on 2017/10/25.
 */
public class MultipartFileConfig {
    private static String fileTempPath;

    public static void setFileTempPath(String fileTempPath) {
        MultipartFileConfig.fileTempPath = fileTempPath;
    }

    public static String getFileTempPath() {
        if (fileTempPath == null || fileTempPath.length() <= 0)
            throw new IllegalArgumentException("please set  fileTempPath");
        return fileTempPath;
    }
}
