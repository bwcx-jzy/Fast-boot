package cn.jiangzeyin.cache;

import cn.jiangzeyin.redis.RedisCacheManagerFactory;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * Created by jiangzeyin on 2017/12/12.
 *
 * @since 1.0.0
 */
public class RedisObjectCache {

    /**
     * 获取默认数据库中的value
     *
     * @param key 获取的key
     * @return object
     */
    public static Object get(String key) {
        int defaultDatabase = RedisCacheConfig.getDefaultDatabase();
        if (defaultDatabase < 0)
            throw new RuntimeException("please config");
        return get(key, defaultDatabase);
    }

    /**
     * 获取指定数据库中的value
     *
     * @param key      key
     * @param database 数据库编号
     * @return object
     */
    public static Object get(String key, int database) {
        Objects.requireNonNull(key);
        if (database < 0)
            throw new RuntimeException("database error");
        RedisCacheManager redisCacheManager = RedisCacheManagerFactory.getRedisCacheManager(database);
        String group = RedisCacheConfig.getKeyGroup(key, database);
        Cache cache = redisCacheManager.getCache(group);
        Cache.ValueWrapper valueWrapper = cache.get(key);
        Object object = null;
        if (valueWrapper != null)
            object = valueWrapper.get();
        if (object != null)
            return object;
        RedisCacheConfig.DataSource dataSource = RedisCacheConfig.getDataSource();
        if (dataSource == null)
            return null;
        object = dataSource.get(key, database);
        if (object != null) {
            cache.put(key, object);
        }
        return object;
    }

    public static void set(String key, Object object) {
        int defaultDatabase = RedisCacheConfig.getDefaultDatabase();
        if (defaultDatabase < 0)
            throw new RuntimeException("please config");
        set(key, object, defaultDatabase);
    }

    public static void set(String key, Object object, int database) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(object, "object");
        if (database < 0)
            throw new RuntimeException("database error");
        RedisCacheManager redisCacheManager = RedisCacheManagerFactory.getRedisCacheManager(database);
        String group = RedisCacheConfig.getKeyGroup(key, database);
        Cache cache = redisCacheManager.getCache(group);
        cache.put(key, object);
    }

    public static void delete(String key) {
        int defaultDatabase = RedisCacheConfig.getDefaultDatabase();
        if (defaultDatabase < 0)
            throw new RuntimeException("please config");
        delete(key, defaultDatabase);
    }

    public static void delete(String key, int database) {
        Objects.requireNonNull(key);
        if (database < 0)
            throw new RuntimeException("database error");
        RedisTemplate redisTemplate = RedisCacheManagerFactory.getRedisTemplate(database);
        redisTemplate.delete(key);
    }

    public static boolean hasKey(String key) {
        int defaultDatabase = RedisCacheConfig.getDefaultDatabase();
        if (defaultDatabase < 0)
            throw new RuntimeException("please config");
        return hasKey(key, defaultDatabase);
    }

    public static boolean hasKey(String key, int database) {
        Objects.requireNonNull(key);
        if (database < 0)
            throw new RuntimeException("database error");
        RedisTemplate redisTemplate = RedisCacheManagerFactory.getRedisTemplate(database);
        return redisTemplate.hasKey(key);
    }
}
