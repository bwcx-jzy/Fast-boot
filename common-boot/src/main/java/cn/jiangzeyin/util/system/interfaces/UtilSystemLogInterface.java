package cn.jiangzeyin.util.system.interfaces;

import ch.qos.logback.classic.Logger;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/7.
 */
public interface UtilSystemLogInterface {

    Logger LOG_INFO();

    Logger LOG_ERROR();
}
