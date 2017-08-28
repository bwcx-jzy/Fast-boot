package cn.jiangzeyin.common;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/6.
 */
public class JsonMessage implements Serializable {
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String DATA = "data";

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


    /**
     * @param code code
     * @param msg  msg
     * @return json
     * @author jiangzeyin
     */
    public static String getString(int code, String msg) {
        return new JsonMessage(code, msg).toString();
    }

    /**
     * @param code code
     * @param msg  msg
     * @param data data
     * @return json
     * @author jiangzeyin
     */
    public static String getString(int code, String msg, Object data) {
        return new JsonMessage(code, msg, data).toString();
    }
}
