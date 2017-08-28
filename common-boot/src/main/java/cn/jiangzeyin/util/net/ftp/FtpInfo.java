package cn.jiangzeyin.util.net.ftp;

import java.util.concurrent.TimeUnit;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/8.
 */
public interface FtpInfo {
    int getPort();

    String getIp();

    String getEncoding();

    String getUserName();

    String getUserPwd();

    int getTimeOut();

    int getMode();

    int getId();

    int getMaxConnects();

    TimeUnit getTimeUnit();
}
