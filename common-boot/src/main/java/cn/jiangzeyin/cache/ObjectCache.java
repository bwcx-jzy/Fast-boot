package cn.jiangzeyin.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiangzeyin on 2017/12/1.
 */
public final class ObjectCache {
    private static final ConcurrentHashMap<String, CacheEntity<String, Object>> CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, CacheInfo> CACHE_INFO_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    public static final int DEFAULT_CACHE_TIME = 1000 * 60 * 10;

    private ObjectCache() {

    }

    public static void config(Class cls) throws IllegalAccessException {
        if (cls == null) throw new NullPointerException();
        CacheConfig cacheConfig = (CacheConfig) cls.getAnnotation(CacheConfig.class);
        Field[] fields = cls.getFields();
        for (Field field : fields) {
            if (field.getType() != String.class)
                continue;
            if (!Modifier.isStatic(field.getModifiers()))
                continue;
            if (!Modifier.isFinal(field.getModifiers()))
                continue;
            CacheConfigField cacheConfigField = field.getAnnotation(CacheConfigField.class);
            String key = (String) field.get(null);
            if (cacheConfigField == null) {
                long cacheTime = cacheConfig != null ? cacheConfig.UNIT().toMillis(cacheConfig.value()) : DEFAULT_CACHE_TIME;
                CacheInfo cacheInfo = new CacheInfo(key, cacheTime);
                CACHE_INFO_CONCURRENT_HASH_MAP.put(key, cacheInfo);
            } else {
                CacheInfo cacheInfo = new CacheInfo(key, cacheConfigField.UNIT().toMillis(cacheConfigField.value()));
                CACHE_INFO_CONCURRENT_HASH_MAP.put(key, cacheInfo);
            }
        }
    }

    public synchronized static void put(String key, Object value) {
        if (key == null) throw new NullPointerException();
        if (value == null) throw new NullPointerException();
        CacheEntity<String, Object> cacheEntity = CONCURRENT_HASH_MAP.get(key);
        if (cacheEntity == null) {
            CacheInfo cacheInfo = CACHE_INFO_CONCURRENT_HASH_MAP.get(key);
            if (cacheInfo == null) {
                cacheInfo = new CacheInfo(key, DEFAULT_CACHE_TIME);
                CACHE_INFO_CONCURRENT_HASH_MAP.put(cacheInfo.getKey(), cacheInfo);
            }
            cacheEntity = new CacheEntity<>(key, value, cacheInfo);
            CONCURRENT_HASH_MAP.put(cacheEntity.getKey(), cacheEntity);
        } else {
            cacheEntity.setValue(value);
        }
    }

    public static Object get(String key) {
        if (key == null) throw new NullPointerException();
        CacheEntity<String, Object> cacheEntity = CONCURRENT_HASH_MAP.get(key);
        return cacheEntity == null ? null : cacheEntity.getValue();
    }

    private static class CacheEntity<K, V> implements Map.Entry<K, V> {
        final K key;
        final CacheInfo cacheInfo;
        V value;
        long intoTime;

        CacheEntity(K key, V value, CacheInfo cacheInfo) {
            this.key = key;
            this.value = value;
            this.intoTime = System.currentTimeMillis();
            this.cacheInfo = cacheInfo;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            long existTime = System.currentTimeMillis() - intoTime;
            if (existTime > cacheInfo.getCacheTime())
                return null;
            return value;
        }

        public V setValue(V value) {
            if (value == null) throw new NullPointerException();
            V val = this.value;
            this.value = value;
            this.intoTime = System.currentTimeMillis();
            return val;
        }
    }

    private static class CacheInfo {
        final String key;
        final long cacheTime;

        CacheInfo(String key, long cacheTime) {
            this.key = key;
            this.cacheTime = cacheTime;
        }

        public String getKey() {
            return key;
        }

        public long getCacheTime() {
            return cacheTime;
        }
    }
}
