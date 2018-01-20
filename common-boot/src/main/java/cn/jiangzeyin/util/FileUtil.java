package cn.jiangzeyin.util;

import org.springframework.util.Assert;

import java.io.*;

/**
 * 文件操作工具类
 *
 * @author jiangzeyin
 */
public final class FileUtil {

    /**
     * 将流写入文件中
     *
     * @param inputStream inp
     * @param file        file
     * @return boolean
     * @throws IOException io
     */
    public static boolean writeInputStream(InputStream inputStream, File file) throws IOException {
        Assert.notNull(inputStream, "inputStream is null");
        Assert.notNull(file, "file is null");
        File parent = file.getParentFile();
        if (!parent.exists())
            if (!parent.mkdirs())
                throw new IllegalArgumentException(file.getPath() + " create fail");
        DataOutputStream outputStream = null;
        try {
            outputStream = new DataOutputStream(new FileOutputStream(file));
            int len = inputStream.available();
            //判断长度是否大于1M
            if (len <= 1024 * 1024) {
                byte[] bytes = new byte[len];
                int rLen = inputStream.read(bytes);
                if (rLen > 0)
                    outputStream.write(bytes);
            } else {
                int byteCount;
                //1M逐个读取
                byte[] bytes = new byte[1024 * 1024];
                while ((byteCount = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, byteCount);
                }
            }
        } finally {
            inputStream.close();
            if (outputStream != null)
                outputStream.close();
        }
        return true;
    }
}

