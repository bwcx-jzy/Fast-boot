package cn.jiangzeyin.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jiangzeyin on 2017/12/12.
 */
public class RedisTemplateFactory {
    private static final ConcurrentHashMap<String, RedisTemplate> REDIS_TEMPLATE_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    public static StringRedisTemplate getStringRedisTemplate(int database) {
        String key = "string_" + database;
        RedisTemplate template = REDIS_TEMPLATE_CONCURRENT_HASH_MAP.get(key);
        if (template != null && template instanceof StringRedisTemplate)
            return (StringRedisTemplate) template;
        template = new StringRedisTemplate();
        RedisConnectionFactory connectionFactory = RedisConnectionFactoryPool.getRedisConnectionFactory(database);
        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet();
        REDIS_TEMPLATE_CONCURRENT_HASH_MAP.put(key, template);
        return (StringRedisTemplate) template;
    }

    public static StringRedisTemplate getStringRedisTemplate() {
        return getStringRedisTemplate(RedisConnectionFactoryPool.getDefaultDatabase());
    }
}
