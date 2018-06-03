package cn.jiangzeyin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 压缩文件信息
 * Created by jiangzeyin on 2018/6/2.
 */
public final class ZipFileUtil {
    /**
     * 获取指定文件的流信息
     *
     * @param path zip 路径
     * @param name entry 名称
     * @return 流
     * @throws IOException Io
     */
    public static InputStream getEntryInputStream(String path, String name) throws IOException {
        try (ZipFile zipFile = new ZipFile(path)) {
            Enumeration<?> zipEntryEnumeration = zipFile.entries();
            while (zipEntryEnumeration.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) zipEntryEnumeration.nextElement();
                if (zipEntry.isDirectory()) {
                    continue;
                }
                String name_ = zipEntry.getName();
                if (name_.equals(name)) {
                    return FileUtil.copyInputStream(zipFile.getInputStream(zipEntry));
                }
            }
        }
        return null;
    }
}
