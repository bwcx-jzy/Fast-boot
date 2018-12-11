package cn.jiangzeyin.cache;

import cn.jiangzeyin.redis.RedisCacheManagerFactory;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 缓存操作封装
 *
 * @author jiangzeyin
 * data 2017/12/12
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
        if (defaultDatabase < 0) {
            throw new RuntimeException("please config");
        }
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
        Cache cache = getCache(key, database);
        Cache.ValueWrapper valueWrapper = cache.get(key);
        Object object = null;
        if (valueWrapper != null) {
            object = valueWrapper.get();
        }
        if (object != null) {
            return object;
        }
        RedisCacheConfig.DataSource dataSource = RedisCacheConfig.getDataSource();
        if (dataSource == null) {
            return null;
        }
        object = dataSource.get(key, database);
        if (object != null) {
            cache.put(key, object);
        }
        return object;
    }

    /**
     * 获取指定缓存后删除本缓存数据
     *
     * @param key      key
     * @param database 数据库编号
     * @return object
     */
    public static Object getAfterDelete(String key, int database) {
        Object object = get(key, database);
        if (object != null) {
            delete(key, database);
        }
        return object;
    }

    /**
     * 添加缓存
     *
     * @param key    缓存的键
     * @param object 缓存的值
     */
    public static void set(String key, Object object) {
        int defaultDatabase = RedisCacheConfig.getDefaultDatabase();
        set(key, object, defaultDatabase);
    }

    private static Cache getCache(String key, int database) {
        if (database < 0) {
            throw new RuntimeException("database error");
        }
        RedisCacheManager redisCacheManager = RedisCacheManagerFactory.getRedisCacheManager(database);
        String group = RedisCacheConfig.getKeyGroup(key, database);
        return redisCacheManager.getCache(group);
    }

    /**
     * 添加缓存
     *
     * @param key      缓存的key
     * @param object   缓存的值
     * @param database 数据库编号
     */
    public static void set(String key, Object object, int database) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(object, "object");
        Cache cache = getCache(key, database);
        cache.put(key, object);
    }

    /**
     * 添加缓存 并指定缓存时间
     *
     * @param key      缓存的key
     * @param object   缓存的值
     * @param database 数据库编号
     * @param time     缓存的时间
     * @param timeUnit 时间单位
     */
    public static void set(String key, Object object, int database, long time, TimeUnit timeUnit) {
        set(key, object, database);
        expire(key, time, timeUnit);
    }

    /**
     * 删除某个缓存
     *
     * @param key 缓存的key
     */
    public static void delete(String key) {
        int defaultDatabase = RedisCacheConfig.getDefaultDatabase();
        delete(key, defaultDatabase);
    }

    /**
     * 删除某个缓存
     *
     * @param key      缓存的key
     * @param database 数据库编号
     */
    public static void delete(String key, int database) {
        Objects.requireNonNull(key);
        if (database < 0) {
            throw new RuntimeException("database error");
        }
        RedisTemplate<String, Object> redisTemplate = RedisCacheManagerFactory.getRedisTemplate(database);
        redisTemplate.delete(key);
    }

    /**
     * 修改缓存的到期时间
     *
     * @param key      缓存的key
     * @param time     时间
     * @param timeUnit 时间单位
     */
    public static void expire(String key, long time, TimeUnit timeUnit) {
        int defaultDatabase = RedisCacheConfig.getDefaultDatabase();
        expire(key, defaultDatabase, time, timeUnit);
    }

    public static void expire(String key, long time) {
        int defaultDatabase = RedisCacheConfig.getDefaultDatabase();
        expire(key, defaultDatabase, time, TimeUnit.SECONDS);
    }

    public static void expire(String key, int database, long time) {
        expire(key, database, time, TimeUnit.SECONDS);
    }

    public static void expire(String key, int database, long time, TimeUnit timeUnit) {
        Objects.requireNonNull(key);
        if (database < 0) {
            throw new RuntimeException("database error");
        }
        RedisTemplate<String, Object> redisTemplate = RedisCacheManagerFactory.getRedisTemplate(database);
        redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 获取到期时间
     *
     * @param key key
     * @return long
     */
    public static Long getExpire(String key) {
        int defaultDatabase = RedisCacheConfig.getDefaultDatabase();
        return getExpire(key, defaultDatabase);
    }

    /**
     * 获取到期时间
     *
     * @param key      key
     * @param database 数据编号
     * @return long
     */
    public static Long getExpire(String key, int database) {
        Objects.requireNonNull(key);
        if (database < 0) {
            throw new RuntimeException("database error");
        }
        RedisTemplate<String, Object> redisTemplate = RedisCacheManagerFactory.getRedisTemplate(database);
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断是否存在某个
     *
     * @param key key
     * @return true
     */
    public static boolean hasKey(String key) {
        int defaultDatabase = RedisCacheConfig.getDefaultDatabase();
        return hasKey(key, defaultDatabase);
    }

    /**
     * 判断是否存在某个
     *
     * @param key      key
     * @param database 数据库编号
     * @return true
     */
    public static boolean hasKey(String key, int database) {
        Objects.requireNonNull(key);
        if (database < 0) {
            throw new RuntimeException("database error");
        }
        RedisTemplate<String, Object> redisTemplate = RedisCacheManagerFactory.getRedisTemplate(database);
        return redisTemplate.hasKey(key);
    }
}
