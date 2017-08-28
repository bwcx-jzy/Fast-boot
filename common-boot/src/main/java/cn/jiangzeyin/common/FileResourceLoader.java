package cn.jiangzeyin.common;

import cn.jiangzeyin.system.SystemBean;
import cn.jiangzeyin.system.log.SystemLog;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/5.
 */
public class FileResourceLoader extends ResourceLoader {
    private static final Map<String, Long> fileLastModified = new HashMap<>();

    @Override
    public void init(ExtendedProperties configuration) {

    }

    @Override
    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        File file = null;
        try {
            file = getResourceFile(source);
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            SystemLog.LOG().info("FileNotFoundException:" + source + "  " + file.getPath());
            return this.getClass().getResourceAsStream(source);
        } finally {
            if (file != null)
                fileLastModified.put(source, file.lastModified());
        }
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        long lastModified = resource.getLastModified();
        File file = getResourceFile(resource.getName());
        return lastModified != file.lastModified();
    }

    @Override
    public long getLastModified(Resource resource) {
        return fileLastModified.get(resource.getName());
    }

    private File getResourceFile(String name) {
        return new File(String.format("%s/%s", SystemBean.getInstance().VelocityPath, name));
    }
}
