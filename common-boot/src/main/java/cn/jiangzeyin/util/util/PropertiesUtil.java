package cn.jiangzeyin.util.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

/**
 * 配置信息工具
 *
 * @author jiangzeyin
 */
public class PropertiesUtil {
    private static final HashMap<String, PropertiesInfo> MAP = new HashMap<>();

    /**
     * 获取配置信息
     *
     * @param name name
     * @return properties
     * @throws URISyntaxException ue
     * @throws IOException        io
     * @author jiangzeyin
     */
    public static OrderedProperties getProperties(String name) throws URISyntaxException, IOException {
        // TODO Auto-generated method stub
        URL url = PropertiesUtil.class.getResource(name);
        return getProperties(url);
    }

    public static OrderedProperties getProperties(URL url) throws IOException, URISyntaxException {
        String type = url.getProtocol();
        if (type.equals("file")) {
            File file = new File(url.toURI());
            return getProperties(file);
        } else if (type.equals("jar")) {
            PropertiesInfo propertiesInfo = MAP.get(url.toString());
            if (propertiesInfo == null) {
                propertiesInfo = new PropertiesInfo();
                InputStream inputStream = url.openStream();
                OrderedProperties properties = new OrderedProperties();
                properties.load(inputStream);
                inputStream.close();
                propertiesInfo.setProperties(properties);
            }
            return propertiesInfo.getProperties();
        }
        throw new IllegalArgumentException("没有对应类型");
    }

    /**
     * @param file file
     * @return p
     * @throws URISyntaxException u
     * @throws IOException        io
     * @author jiangzeyin
     */
    public static OrderedProperties getProperties(File file) throws URISyntaxException, IOException {
        // TODO Auto-generated method stub
        PropertiesInfo propertiesInfo = MAP.get(file.getPath());
        if (propertiesInfo == null) {
            // 创建对象信息
            propertiesInfo = new PropertiesInfo();
            // URI uri = PropertiesUtil.class.getResource(name).toURI();
            InputStream inputStream = new FileInputStream(file);// File file = new File(inputStream);
            OrderedProperties properties = new OrderedProperties(file);
            properties.load(inputStream);
            inputStream.close();
            //
            propertiesInfo.setFile(file);
            propertiesInfo.setLastModified(file.lastModified());
            propertiesInfo.setProperties(properties);
            //
            MAP.put(file.getPath(), propertiesInfo);
        } else {
            // 判断文件是否变化
            if (propertiesInfo.getFile().lastModified() != propertiesInfo.getLastModified()) {
                InputStream in = new FileInputStream(propertiesInfo.getFile());
                OrderedProperties properties = propertiesInfo.getProperties();
                properties.load(in);
                in.close();
            }
        }
        return propertiesInfo.getProperties();
    }

    public static class PropertiesInfo {
        private OrderedProperties properties;
        private long lastModified;
        private File file;

        public OrderedProperties getProperties() {
            return properties;
        }

        public void setProperties(OrderedProperties properties) {
            this.properties = properties;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }
}
