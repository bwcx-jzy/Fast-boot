package cn.jiangzeyin.j2cache;

import cn.hutool.core.util.CharsetUtil;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

/**
 * j2Cache 创建channel 代理
 *
 * @author jiangzeyin
 * @date 2019/2/27
 */
public class J2CacheChannelProxy {
    /**
     * 使用 Properties 创建配置对象
     *
     * @param properties key-value
     * @return J2CacheConfig
     * @throws IOException IO
     */
    public static J2CacheConfig initFromConfig(Properties properties) throws IOException {
        StringWriter stringWriter = new StringWriter();
        properties.store(stringWriter, "");
        byte[] bytes = stringWriter.toString().getBytes(CharsetUtil.CHARSET_UTF_8);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return J2CacheConfig.initFromConfig(byteArrayInputStream);
    }

    /**
     * 使用 Properties 创建Channel
     *
     * @param properties key-value
     * @return CacheChannel
     * @throws IOException IO
     */
    public static CacheChannel getChannel(Properties properties) throws IOException {
        J2CacheConfig j2CacheConfig = initFromConfig(properties);
        return J2CacheBuilder.init(j2CacheConfig).getChannel();
    }
}
