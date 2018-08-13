package cn.jiangzeyin.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * 通用的json 数据格式
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/6.
 */
public class JsonMessage implements Serializable {
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String DATA = "data";

    static {
        // long 类型自动转String
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(long.class, ToStringSerializer.instance);
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
    }

    private int code;
    private String msg;
    private Object data;

    public JsonMessage(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public JsonMessage(int code, String msg) {
        this(code, msg, null);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return json
     * @author jiangzeyin
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return JSONObject.toJSONString(this);
    }

    public JSONObject toJson() {
        return (JSONObject) JSONObject.toJSON(this);
    }

    /**
     * 输出格式化后的json 字符串
     *
     * @return 字符串
     */
    public String toFormatJson() {
        return JSONObject.toJSONString(this, SerializerFeature.PrettyFormat);
    }

    public static JSONObject toJson(int code, String msg) {
        return toJson(code, msg, null);
    }

    public static JSONObject toJson(int code, String msg, Object data) {
        JsonMessage jsonMessage = new JsonMessage(code, msg, data);
        return jsonMessage.toJson();
    }

    /**
     * @param code code
     * @param msg  msg
     * @return json
     * @author jiangzeyin
     */
    public static String getString(int code, String msg) {
        return getString(code, msg, null);
    }

    /**
     * @param code code
     * @param msg  msg
     * @param data data
     * @return json
     * @author jiangzeyin
     */
    public static String getString(int code, String msg, Object data) {
        return toJson(code, msg, data).toString();
    }
}
