package cn.jiangzeyin.util.system.interfaces;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/16.
 */
public interface UtilSystemExecutorService {

    ThreadPoolExecutor newCachedThreadPool(Class cls);
}
