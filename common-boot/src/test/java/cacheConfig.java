import cn.jiangzeyin.cache.CacheConfig;
import cn.jiangzeyin.cache.CacheConfigField;

import java.util.concurrent.TimeUnit;

/**
 * Created by jiangzeyin on 2017/12/1.
 */
@CacheConfig(value = 5, UNIT = TimeUnit.MINUTES)
public class cacheConfig {
    // sss缓存 根据class 的属性读取
    public static final String SSS = "sss";
    // ttt 缓存1 天
    @CacheConfigField(value = 1, UNIT = TimeUnit.DAYS)
    public static final String TTT = "ttt";
}
