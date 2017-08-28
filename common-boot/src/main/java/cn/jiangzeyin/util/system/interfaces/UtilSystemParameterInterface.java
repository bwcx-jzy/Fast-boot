package cn.jiangzeyin.util.system.interfaces;

import com.alibaba.fastjson.JSONObject;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/7.
 */
public interface UtilSystemParameterInterface {

    String getSystemParameterValue(String name);

    String getSystemParameterValue(String name, String def);

    JSONObject systemParameterToJSONObject(String name);
}
