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

    public synchronized static Object put(String key, Object value) {
        return put(key, value, -1);
    }

    public synchronized static Object put(String key, Object value, long cacheTime) {
        if (key == null) throw new NullPointerException();
        if (value == null) throw new NullPointerException();
        if (cacheTime < -1)
            throw new IllegalArgumentException("cacheTime must >=0");
        CacheEntity<String, Object> cacheEntity = CONCURRENT_HASH_MAP.get(key);
        if (cacheEntity == null) {
            CacheInfo cacheInfo = CACHE_INFO_CONCURRENT_HASH_MAP.get(key);
            if (cacheInfo == null) {
                cacheInfo = new CacheInfo(key, cacheTime == -1 ? DEFAULT_CACHE_TIME : cacheTime);
                CACHE_INFO_CONCURRENT_HASH_MAP.put(cacheInfo.getKey(), cacheInfo);
            } else if (cacheTime != -1) {
                cacheInfo.cacheTime = cacheTime;
            }
            cacheEntity = new CacheEntity<>(key, value, cacheInfo);
            CONCURRENT_HASH_MAP.put(cacheEntity.getKey(), cacheEntity);
            return null;
        }
        return cacheEntity.setValue(value, cacheTime);
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

        V setValue(V value, long cacheTime) {
            if (cacheTime != -1)
                this.cacheInfo.cacheTime = cacheTime;
            return setValue(value);
        }
    }

    private static class CacheInfo {
        final String key;
        long cacheTime;

        CacheInfo(String key, long cacheTime) {
            this.key = key;
            this.cacheTime = cacheTime;
        }

        String getKey() {
            return key;
        }

        long getCacheTime() {
            return cacheTime;
        }
    }
}
