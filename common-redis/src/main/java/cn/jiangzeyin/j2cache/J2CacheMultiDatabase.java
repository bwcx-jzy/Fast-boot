package cn.jiangzeyin.j2cache;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import net.oschina.j2cache.CacheChannel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * j2Cache 使用多database
 *
 * @author jiangzeyin
 * @date 2019/2/27
 */
public class J2CacheMultiDatabase {
    private static Properties properties;
    private static final ConcurrentHashMap<Integer, CacheChannel> CACHE_CHANNEL_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    /**
     * 初始化全局配置
     *
     * @param properties properties
     */
    public static void initApplicationConfig(Properties properties) {
        J2CacheMultiDatabase.properties = properties;
    }

    /**
     * 初始化全局配置
     *
     * @param resurce 资源路径
     */
    public static void initApplicationConfig(String resurce) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = ResourceUtil.getStream(resurce);
        properties.load(inputStream);
        initApplicationConfig(properties);
    }

    /**
     * 获取指定database channel
     *
     * @param database 索引号
     * @return CacheChannel
     */
    public static CacheChannel getChannel(int database) {
        Objects.requireNonNull(properties, "请初始全局配置:initApplicationConfig");
        if (database < 0 || database > 255) {
            throw new IllegalArgumentException("0-255");
        }
        CacheChannel cacheChannel = CACHE_CHANNEL_CONCURRENT_HASH_MAP.computeIfAbsent(database, integer -> {
            Properties nowProperties = (Properties) properties.clone();
            nowProperties.setProperty("redis.database", integer.toString());
            //
            String redisChannel = properties.getProperty("redis.channel");
            nowProperties.setProperty("redis.channel", redisChannel + "_" + integer);
            try {
                return J2CacheChannelProxy.getChannel(nowProperties);
            } catch (IOException e) {
                DefaultSystemLog.ERROR().error("channel 异常", e);
            }
            return null;
        });
        Objects.requireNonNull(cacheChannel, "初始化失败:" + database);
        return cacheChannel;
    }
}
