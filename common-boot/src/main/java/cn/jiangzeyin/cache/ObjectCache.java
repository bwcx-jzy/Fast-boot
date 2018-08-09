package cn.jiangzeyin.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 内存缓存
 *
 * @author jiangzeyin
 * data 2017/12/1
 */
public final class ObjectCache {
    private static final ConcurrentHashMap<String, CacheEntity<String, Object>> CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, CacheInfo> CACHE_INFO_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    /**
     * 默认10分钟  秒单位
     */
    static final int DEFAULT_CACHE_TIME = 60 * 10;

    private ObjectCache() {

    }

    public static void config(Class cls) throws IllegalAccessException {
        CACHE_INFO_CONCURRENT_HASH_MAP.putAll(CacheInfo.loadClass(cls));
    }

    public static Object put(String key, Object value) {
        return put(key, value, -1);
    }

    /**
     * 添加缓存信息
     *
     * @param key       键
     * @param value     值
     * @param cacheTime 缓存时间
     * @return 上传缓存对象
     */
    public static Object put(String key, Object value, long cacheTime) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (value == null) {
            throw new NullPointerException();
        }
        if (cacheTime < -1) {
            throw new IllegalArgumentException("cacheTime must >=0");
        }
        AtomicBoolean containsKey = new AtomicBoolean(true);
        CacheEntity<String, Object> cacheEntity = CONCURRENT_HASH_MAP.computeIfAbsent(key, entityKey -> {
            // 缓存信息
            CacheInfo cacheInfo = CACHE_INFO_CONCURRENT_HASH_MAP.computeIfAbsent(key, s -> new CacheInfo(key, cacheTime == -1 ? DEFAULT_CACHE_TIME : cacheTime));
            if (cacheTime != cacheInfo.getCacheTime()) {
                cacheInfo.setCacheTime(cacheTime);
            }
            containsKey.set(false);
            // 缓存对象
            return new CacheEntity<>(key, value, cacheInfo);
        });
        return containsKey.get() ? cacheEntity.setValue(value, cacheTime) : null;
    }

    public static Object get(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
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
            this.intoTime = getCurrentTime();
            this.cacheInfo = cacheInfo;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            long existTime = getCurrentTime() - intoTime;
            if (existTime > cacheInfo.getCacheTime()) {
                return null;
            }
            return value;
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            V val = this.value;
            this.value = value;
            this.intoTime = getCurrentTime();
            return val;
        }

        V setValue(V value, long cacheTime) {
            if (cacheTime != -1) {
                this.cacheInfo.setCacheTime(cacheTime);
            }
            return setValue(value);
        }
    }

    private static long getCurrentTime() {
        return System.currentTimeMillis() / 1000L;
    }
}
