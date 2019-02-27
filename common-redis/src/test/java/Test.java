import cn.hutool.core.io.resource.ResourceUtil;
import cn.jiangzeyin.j2cache.J2CacheChannelProxy;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jiangzeyin on 2019/2/27.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = ResourceUtil.getStream("/j2cache.properties");
        properties.load(inputStream);
        properties.setProperty("redis.hosts", "");
        properties.setProperty("redis.password", "");
        properties.setProperty("redis.database", "2");


        J2CacheConfig config_admin = J2CacheChannelProxy.initFromConfig(properties);
        config_admin.dump(System.out);

        System.out.println("------------------------------");
        config_admin.dump(System.out);
        CacheChannel channel_admin = J2CacheBuilder.init(config_admin).getChannel();
        channel_admin.clear("test");

        channel_admin.set("test", "test", "1");
        System.out.println("run1-0:" + channel_admin.get("test", "test2"));
        System.out.println("run1-1:" + channel_admin.get("test", "test"));

        //

        System.out.println("run2-1:" + channel_admin.get("test", "test"));


        System.out.println("run3-1:" + channel_admin.get("test", "test"));

        //
        channel_admin.set("test", "test", "3");
        System.out.println("run4-1:" + channel_admin.get("test", "test"));
    }
}
