package cn.jiangzeyin;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

/**
 * @author jiangzeyin
 * @date 2017/1/5.
 */
@Configuration
public class FileResourceLoader extends ResourceLoader {


    @Override
    public void init(ExtendedProperties extendedProperties) {

    }

    @Override
    public InputStream getResourceStream(String s) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        return false;
    }

    @Override
    public long getLastModified(Resource resource) {
        return 0;
    }
}
