package cn.jiangzeyin.util;

import cn.hutool.core.io.IoUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 压缩文件信息
 * Created by jiangzeyin on 2018/6/2.
 *
 * @author jiangzeyin
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
                String entityName = zipEntry.getName();
                if (entityName.equals(name)) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    IoUtil.copy(zipFile.getInputStream(zipEntry), byteArrayOutputStream);
                    InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException ignored) {
                    }
                    return inputStream;
                }
            }
        }
        return null;
    }

//    /**
//     * 复制流
//     *
//     * @param input input
//     * @return 新流
//     * @throws IOException io
//     */
//    public static InputStream copyInputStream(InputStream input) throws IOException {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        int len = input.available();
//        //判断长度是否大于1M  如果大于1M逐个读取
//        int byteLen = len > EACH_LEN ? EACH_LEN : len;
//        byte[] bytes = new byte[byteLen];
//        //  byte[] buffer = new byte[1024];
//        while ((len = input.read(bytes)) > -1) {
//            byteArrayOutputStream.write(bytes, 0, len);
//        }
//        byteArrayOutputStream.flush();
//        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//    }
}
