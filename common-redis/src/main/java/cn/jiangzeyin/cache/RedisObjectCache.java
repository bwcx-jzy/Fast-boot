package cn.jiangzeyin.cache;

import cn.jiangzeyin.redis.RedisTemplateFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiangzeyin on 2017/12/12.
 */
public class RedisObjectCache {
    // 普通缓存key
    private static final ConcurrentHashMap<String, CacheInfo> CACHE_INFO_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, CacheInfo> WILDCARD_MAP = new ConcurrentHashMap<>();
    private static int defaultDatabase = 0;
    private static DataSource dataSource;
    private volatile static RedisTemplate<String, Object> redisTemplate;

    public static void config(Class cls, int database, DataSource source) throws IllegalAccessException {
        CACHE_INFO_CONCURRENT_HASH_MAP.putAll(CacheInfo.loadClass(cls));
        defaultDatabase = database;
        dataSource = source;
        Map<String, CacheInfo> map = new HashMap<>();
        Field[] fields = cls.getFields();
        for (Field field : fields) {
            if (field.getType() != String.class)
                continue;
            if (!Modifier.isStatic(field.getModifiers()))
                continue;
            if (!Modifier.isFinal(field.getModifiers()))
                continue;
            CacheConfigWildcardField wildcardField = field.getAnnotation(CacheConfigWildcardField.class);
            if (wildcardField == null)
                continue;
            String key = (String) field.get(null);
            // 毫秒
            long cacheTime = wildcardField.UNIT().toMillis(wildcardField.value());
            CacheInfo cacheInfo = new CacheInfo(key, cacheTime);
            map.put(key, cacheInfo);
        }
        WILDCARD_MAP.putAll(map);
    }

    private static void doRedisTemplate() {
        if (redisTemplate == null) {
            synchronized (RedisObjectCache.class) {
                if (redisTemplate == null) {
                    redisTemplate = RedisTemplateFactory.getRedisTemplate(defaultDatabase);
                }
            }
        }
    }

    public static Object get(String key) {
        if (key == null) throw new NullPointerException();
        doRedisTemplate();
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object object = valueOperations.get(key);
        if (object != null)
            return object;
        if (dataSource == null)
            return null;
        object = dataSource.get(key);
        if (object != null) {
            // 缓存到redis 中
            CacheInfo cacheInfo = CACHE_INFO_CONCURRENT_HASH_MAP.get(key);
            if (cacheInfo == null)
                cacheInfo = wildcard(key);
            long time = cacheInfo == null ? ObjectCache.DEFAULT_CACHE_TIME : cacheInfo.getCacheTime();
            valueOperations.set(key, object, time, TimeUnit.MILLISECONDS);
        }
        return object;
    }

    private static CacheInfo wildcard(String key) {
        Enumeration<String> enumeration = WILDCARD_MAP.keys();
        while (enumeration.hasMoreElements()) {
            String next = enumeration.nextElement();
            if (key.startsWith(next))
                return WILDCARD_MAP.get(key);
        }
        return null;
    }

    /**
     * redis 数据源接口
     */
    public interface DataSource {
        // 根据key 获取数据
        Object get(String key);
    }
}
