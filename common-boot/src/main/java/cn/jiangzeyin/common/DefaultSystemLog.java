package cn.jiangzeyin.common;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import cn.jiangzeyin.common.spring.SpringUtil;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统日志
 *
 * @author jiangzeyin
 * @date 2017/2/3.
 */
public class DefaultSystemLog {
    private static final LoggerContext LOGGER_CONTEXT = (LoggerContext) LoggerFactory.getILoggerFactory();
    private static final Map<LogType, Logger> LOG_TYPE_LOGGER_MAP = new ConcurrentHashMap<>();
    private static final String TYPE_ERROR_TAG = "ERROR";
    private static String LOG_PATH = "/log/cn.jiangzeyin";
    private static boolean appendApplicationId = true;
    private static ConsoleAppender<ILoggingEvent> consoleAppender;
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
     * 配置默认日志文件路径
     *
     * @param path                路径
     * @param appendApplicationId 路径是否追加应用id
     * @since 1.2.27
     */
    public static void configPath(String path, boolean appendApplicationId) {
        DefaultSystemLog.LOG_PATH = path;
        DefaultSystemLog.appendApplicationId = appendApplicationId;
    }

    /**
     * 加载日志
     */
    public static void init() {
        consoleAppender = initConsole();
        initSystemLog();
    }

    /**
     * 加载系统日志文件对象
     */
    private static void initSystemLog() {
        for (LogType type : LogType.values()) {
            String tag = type.toString();
            Level level = Level.INFO;
            if (tag.endsWith(TYPE_ERROR_TAG)) {
                level = Level.ERROR;
            }
            Logger logger = initLogger(tag, level);
            LOG_TYPE_LOGGER_MAP.put(type, logger);
        }
    }

    /**
     * 加载控制显示
     *
     * @return r
     */
    private static ConsoleAppender<ILoggingEvent> initConsole() {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(LOGGER_CONTEXT);
        patternLayout.setPattern("%date %level [%thread] %logger{10} [%file:%line]- x:\\(%X\\) %msg%n");
        patternLayout.start();
        appender.setLayout(patternLayout);
        appender.setContext(LOGGER_CONTEXT);
        appender.start();
        return appender;
    }

    /**
     * 创建日志对象
     *
     * @param tag   日志标记
     * @param level 日志级别
     * @return logger
     */
    private static Logger initLogger(String tag, Level level) {
        Logger logger = LOGGER_CONTEXT.getLogger(tag);
        logger.detachAndStopAllAppenders();
        logger.setLevel(level);
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setDiscardingThreshold(0);
        asyncAppender.setQueueSize(512);
        //define appender
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        //policy
        SizeAndTimeBasedRollingPolicy<Object> policy = new SizeAndTimeBasedRollingPolicy<>();
        policy.setContext(LOGGER_CONTEXT);

        String filePath = String.format("%s/%s/%s/%s", LOG_PATH, appendApplicationId ? SpringUtil.getApplicationId() : "", tag, tag).toLowerCase();
        String fileNamePattern = String.format("%s-%%d{yyyy-MM-dd}.%%i.log", filePath);
        policy.setFileNamePattern(fileNamePattern);
        policy.setMaxFileSize(FileSize.valueOf("100MB"));
        policy.setMaxHistory(30);
        policy.setTotalSizeCap(FileSize.valueOf("10GB"));
        policy.setParent(appender);
        policy.start();
        //encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(LOGGER_CONTEXT);
        encoder.setPattern("%d{HH:mm:ss.SSS} %-5level [%thread %file:%line] %logger - %msg%n");
        encoder.start();
        appender.setFile(String.format("%s.log", filePath));
        appender.setName("appender" + tag);
        appender.setRollingPolicy(policy);
        appender.setContext(LOGGER_CONTEXT);
        appender.setEncoder(encoder);
        //support that multiple JVMs can safely write to the same file.
        appender.setPrudent(true);
        appender.start();
        asyncAppender.addAppender(appender);
        asyncAppender.start();
        logger.addAppender(asyncAppender);
        if (level == Level.ERROR) {
            logger.addAppender(consoleAppender);
        }
        //setup level
        // newLogger.setLevel(Level.ERROR);
        // remove the appenders that inherited 'ROOT'.
        logger.setAdditive(true);
        return logger;
    }

    /**
     * 获取系统日志
     *
     * @param type type
     * @return logger
     */
    public static Logger LOG(LogType type) {
        Logger logger = LOG_TYPE_LOGGER_MAP.get(type);
        if (logger == null) {
            if (type == LogType.DEFAULT) {
                logger = LOGGER_CONTEXT.getLogger(DefaultSystemLog.class);
            } else {
                logger = LOG(LogType.DEFAULT);
            }
        }
        return logger;
    }

    public static Logger LOG() {
        return LOG(LogType.DEFAULT);
    }

    public static Logger ERROR() {
        return LOG(LogType.ERROR);
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
