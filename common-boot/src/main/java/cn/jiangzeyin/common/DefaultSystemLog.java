package cn.jiangzeyin.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.util.Map;

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
        REQUEST,
        REQUEST_ERROR,
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

        /**
         * 请求开始
         *
         * @param id         请求id
         * @param url        url
         * @param httpMethod 方法
         * @param ip         ip
         * @param parameters 参数
         * @param header     请求头
         */
        void logStart(String id, String url, HttpMethod httpMethod, String ip, Map<String, String> parameters, Map<String, String> header);

        /**
         * 请求错误
         *
         * @param id     请求id
         * @param status 状态码
         */
        void logError(String id, int status);

        /**
         * 请求超时
         *
         * @param id   请求id
         * @param time 时间
         */
        void logTimeOut(String id, long time);
    }
}
