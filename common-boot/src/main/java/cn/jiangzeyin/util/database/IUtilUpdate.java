package cn.jiangzeyin.util.database;

import java.util.HashMap;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/16.
 */
public interface IUtilUpdate {

    long update(Class cls, HashMap<String, Object> map, String keyColumn, Object keyValue);
}
