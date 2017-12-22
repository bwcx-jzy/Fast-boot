package cn.jiangzeyin.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiangzeyin on 2017/12/20.
 *
 * @since 1.0.1
 */
public class RedisCacheConfig {
    // 普通缓存key
    private static final ConcurrentHashMap<Integer, Map<String, Long>> CACHE_INFO_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    private static Map<Integer, Long> defaultExpireTime;
    private static int defaultDatabase = -1;
    private static DataSource dataSource;
    private static ConvertKey convertKey;

    /**
     * 配置缓存的 key cls  和默认的database 以及数据源接口
     *
     * @param cls      缓存key 的静态字符串
     * @param database 默认数据编号
     * @param source   数据源
     * @throws IllegalAccessException e
     */
    public static void config(Class cls, int database, ConvertKey convertKey, DataSource source) throws IllegalAccessException {
        if (database < 0)
            throw new IllegalArgumentException("database error");
        defaultDatabase = database;
        config(cls, convertKey, source);
    }

    /**
     * @param cls    缓存key 的静态字符串
     * @param source 数据源接口
     * @throws IllegalAccessException e
     */
    public static void config(Class cls, ConvertKey key, DataSource source) throws IllegalAccessException {
        dataSource = source;
        convertKey = key;
        loadClass(cls);
    }

    public static Map<String, Long> getExpires(int database) {
        return CACHE_INFO_CONCURRENT_HASH_MAP.get(database);
    }

    public static Long getGroupExpires(int database, String group) {
        Map<String, Long> map = CACHE_INFO_CONCURRENT_HASH_MAP.get(database);
        if (map == null)
            return null;
        return map.get(group);
    }

    public static Long getDefaultExpireTime(int database) {
        if (defaultExpireTime == null)
            return (long) ObjectCache.DEFAULT_CACHE_TIME;
        Long time = defaultExpireTime.get(database);
        if (time == null)
            return (long) ObjectCache.DEFAULT_CACHE_TIME;
        return time;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static int getDefaultDatabase() {
        return defaultDatabase;
    }

    /**
     * 加载缓存key 配置 class
     *
     * @param cls class
     * @throws IllegalAccessException e
     */
    private static void loadClass(Class cls) throws IllegalAccessException {
        Field[] fields = cls.getFields();
        for (Field field : fields) {
            Class type = field.getType();
            if (type == String.class) {
                if (!Modifier.isStatic(field.getModifiers()))
                    continue;
                if (!Modifier.isFinal(field.getModifiers()))
                    continue;
                CacheConfigWildcardField wildcardField = field.getAnnotation(CacheConfigWildcardField.class);
                if (wildcardField == null)
                    continue;
                String key = (String) field.get(null);
                Map<String, Long> map = CACHE_INFO_CONCURRENT_HASH_MAP.computeIfAbsent(wildcardField.value(), k -> new HashMap<>());
                // 秒
                long cacheTime = wildcardField.UNIT().toSeconds(wildcardField.time());
                map.put(key, cacheTime);
            } else if (type == Map.class) {
                defaultExpireTime = (Map) field.get(null);
            }
        }
    }

    /**
     * 根据key 获取 key 的group
     *
     * @param key key
     * @return group
     */
    public static String getKeyGroup(String key, int database) {
        if (convertKey == null)
            return "default";
        return convertKey.getGroup(key, database);
    }

    public interface ConvertKey {
        String getGroup(String key, int database);
    }

    /**
     * redis 数据源接口
     */
    public interface DataSource {
        /**
         * 根据key 获取数据  指定数据库
         *
         * @param key      key
         * @param database 数据库编号
         * @return object
         */
        Object get(String key, int database);

    }
}
