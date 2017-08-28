package cn.jiangzeyin.util.system.interfaces;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/7.
 */
public interface UtilSiteCacheInterface {

    String getSiteUrl(String tag);

    String getLocalPath(String tag);

    int getCurrentSiteId();

}
