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
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/3.
 */
public class DefaultSystemLog {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    private static final Map<LogType, Logger> LOG_TYPE_LOGGER_MAP = new ConcurrentHashMap<>();
    private static final String TYPE_ERROR_TAG = "ERROR";
    private static ConsoleAppender<ILoggingEvent> consoleAppender;

    public enum LogType {
        REQUEST, REQUEST_ERROR,
        DEFAULT, ERROR
    }


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
            Logger logger = initLogger(tag, tag, level);
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
        patternLayout.setContext(loggerContext);
        patternLayout.setPattern("%date %level [%thread] %logger{10} [%file:%line]- x:\\(%X\\) %msg%n");
        patternLayout.start();
        appender.setLayout(patternLayout);
        appender.setContext(loggerContext);
        appender.start();
        return appender;
    }

    /**
     * 创建日志对象
     *
     * @param tag   tag
     * @param path  path
     * @param level lv
     * @return logger
     */
    private static Logger initLogger(String tag, String path, Level level) {
        Logger logger = (Logger) LoggerFactory.getLogger(tag);
        logger.detachAndStopAllAppenders();
        logger.setLevel(level);
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setDiscardingThreshold(0);
        asyncAppender.setQueueSize(512);
        //define appender
        RollingFileAppender appender = new RollingFileAppender<>();
        //policy
        SizeAndTimeBasedRollingPolicy<Object> policy = new SizeAndTimeBasedRollingPolicy<>();
        policy.setContext(loggerContext);
        String logPath = "/log/cn.jiangzeyin";
        policy.setFileNamePattern(String.format("%s/%s/%s/%s-%%d{yyyy-MM-dd}.%%i.log", logPath, SpringUtil.getApplicationId(), path, tag).toLowerCase());
        policy.setMaxFileSize("100MB");
        policy.setMaxHistory(30);
        policy.setTotalSizeCap(FileSize.valueOf("10GB"));
        policy.setParent(appender);
        policy.start();
        //encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%d{HH:mm:ss.SSS} %-5level [%thread %file:%line] %logger - %msg%n");
        encoder.start();
        appender.setFile(String.format("%s/%s/%s/%s.log", logPath, SpringUtil.getApplicationId(), path, tag).toLowerCase());
        appender.setName("appender" + tag);
        appender.setRollingPolicy(policy);
        appender.setContext(loggerContext);
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
        //remove the appenders that inherited 'ROOT'.
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
        if (logger == null && LogType.DEFAULT != type)
            return LOG(LogType.DEFAULT);
        return logger;
    }

    public static Logger LOG() {
        return LOG(LogType.DEFAULT);
    }

    public static Logger ERROR() {
        return LOG(LogType.ERROR);
    }
}
