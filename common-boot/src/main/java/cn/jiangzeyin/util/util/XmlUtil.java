package cn.jiangzeyin.util.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * xml util
 *
 * @author jiangzeyin
 */
public class XmlUtil {
    // 创建SAXReader对象
    private static SAXReader reader = new SAXReader();

    /**
     * @param path path
     * @return r
     * @throws MalformedURLException mue
     * @throws DocumentException     de
     * @author jiangzeyin
     */
    public static Document load(String path) throws MalformedURLException, DocumentException {
        if (path == null)
            return null;
        return load(new File(path));
    }

    /**
     * @param file file
     * @return doc
     * @throws MalformedURLException mre
     * @throws DocumentException     doc
     * @author jiangzeyin
     */
    public static Document load(File file) throws MalformedURLException, DocumentException {
        if (!file.exists())
            return null;
        // 读取文件 转换成Document
        Document document = reader.read(file);
        return document;
    }

    /**
     * @param inputStream inp
     * @return doc
     * @throws DocumentException de
     */
    public static Document load(InputStream inputStream) throws DocumentException {
        Assert.notNull(inputStream);
        return reader.read(inputStream);
    }
}
