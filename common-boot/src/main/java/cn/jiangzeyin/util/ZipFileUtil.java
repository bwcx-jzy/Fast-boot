package cn.jiangzeyin.util;

import cn.hutool.core.util.ZipUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
     */
    public static InputStream getEntryInputStream(String path, String name) {
        byte[] bytes = ZipUtil.unzipFileBytes(path, name);
        return new ByteArrayInputStream(bytes);
    }
}
