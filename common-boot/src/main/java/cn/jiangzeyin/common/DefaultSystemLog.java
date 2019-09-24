package cn.jiangzeyin.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统日志
 *
 * @author jiangzeyin
 * @date 2017/2/3.
 */
public class DefaultSystemLog {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSystemLog.class);

    private static volatile LogCallback logCallback;

    private DefaultSystemLog() {
    }

    /**
     * 日志类型
     */
    public enum LogType {
        /**
         * 请求
         */
        REQUEST, REQUEST_ERROR,
        /**
         * 默认
         */
        DEFAULT,
        /**
         * 异常
         */
        ERROR
    }

    public static void setLogCallback(LogCallback logCallback) {
        DefaultSystemLog.logCallback = logCallback;
    }

    public static LogCallback getLogCallback() {
        return logCallback;
    }


    /**
     * 获取系统日志
     *
     * @return logger
     */
    public static Logger getLog() {
        return LOG;
    }


    /**
     * 日志回调
     *
     * @since 1.2.34
     */
    public interface LogCallback {

        /**
         * 普通日志
         *
         * @param type 日志类型
         * @param log  日志信息
         */
        void log(LogType type, Object... log);
    }
}
