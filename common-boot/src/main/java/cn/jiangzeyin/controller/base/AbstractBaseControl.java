package cn.jiangzeyin.controller.base;

import cn.hutool.core.convert.Convert;
import cn.hutool.extra.servlet.ServletUtil;
import cn.jiangzeyin.common.interceptor.BaseCallbackController;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * base
 * 公共的获取参数
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/12.
 */
public abstract class AbstractBaseControl extends BaseCallbackController {


    /**
     * 拦截器注入
     */
    @Override
    public void resetInfo() {
        super.resetInfo();
    }

    /**
     * 获取请求的ip 地址
     *
     * @return ip
     */
    protected String getIp() {
        return ServletUtil.getClientIP(getRequest());
    }

    protected String getHeader(String name) {
        return getRequest().getHeader(name);
    }


    protected String getCookieValue(String name) {
        Cookie cookie = ServletUtil.getCookie(getRequest(), name);
        if (cookie == null) {
            return "";
        }
        return cookie.getValue();
    }

    protected String getParameter(String name) {
        return getParameter(name, null);
    }

    protected String[] getParameters(String name) {
        return getRequest().getParameterValues(name);
    }

    /**
     * 获取指定参数名的值
     *
     * @param name 参数名
     * @param def  默认值
     * @return str
     */
    protected String getParameter(String name, String def) {
        String value = getRequest().getParameter(name);
        return value == null ? def : value;
    }

    protected int getParameterInt(String name, int def) {
        return Convert.toInt(getParameter(name), def);
    }

    protected int getParameterInt(String name) {
        return getParameterInt(name, 0);
    }

    protected long getParameterLong(String name, long def) {
        String value = getParameter(name);
        if (value == null) {
            return def;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
        }
        return def;
    }

    protected long getParameterLong(String name) {
        return getParameterLong(name, 0L);
    }

    /**
     * 获取来源的url 参数
     *
     * @return map
     * @throws UnsupportedEncodingException 编码异常
     */
    protected Map<String, String> getRefererParameter() throws UnsupportedEncodingException {
        String referer = getHeader(HttpHeaders.REFERER);
        return RequestUtil.convertUrlMap(referer);
    }

    /**
     * 获取表单数据到实体中
     *
     * @param tClass class
     * @param <T>    t
     * @return t
     */
    protected <T> T getObject(Class<T> tClass) {
        return ServletUtil.toBean(getRequest(), tClass, true);
    }
}
