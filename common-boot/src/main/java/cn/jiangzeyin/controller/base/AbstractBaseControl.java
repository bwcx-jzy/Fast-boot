package cn.jiangzeyin.controller.base;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.spring.SpringUtil;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/12.
 */
public abstract class AbstractBaseControl {
    private static final ThreadLocal<HttpServletRequest> HTTP_SERVLET_REQUEST_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<HttpSession> HTTP_SESSION_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> HTTP_SERVLET_RESPONSE_THREAD_LOCAL = new ThreadLocal<>();

    protected String ip;

    public HttpServletResponse getResponse() {
        return HTTP_SERVLET_RESPONSE_THREAD_LOCAL.get();
    }

    public HttpSession getSession() {
        return HTTP_SESSION_THREAD_LOCAL.get();
    }

    public HttpServletRequest getRequest() {
        return HTTP_SERVLET_REQUEST_THREAD_LOCAL.get();
    }

    protected Object getAttribute(String name) {
        return getRequest().getAttribute(name);
    }

    protected void setAttribute(String name, Object object) {
        getRequest().setAttribute(name, object);
    }

    /**
     * 拦截器注入
     *
     * @param request  req
     * @param session  ses
     * @param response rep
     */
    public void setReqAndRes(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        HTTP_SERVLET_REQUEST_THREAD_LOCAL.set(request);
        HTTP_SESSION_THREAD_LOCAL.set(session);
        HTTP_SERVLET_RESPONSE_THREAD_LOCAL.set(response);
        this.ip = getIpAddress(request);
        response.setCharacterEncoding("UTF-8");
    }

    protected String getHeader(String name) {
        return getRequest().getHeader(name);
    }

    public void reLoad() {

    }

    /**
     * 获取session 字符串
     *
     * @param name name
     * @return str
     * @author jiangzeyin
     */
    protected String getSessionAttribute(String name) {
        Object obj = getSession().getAttribute(name);
        if (obj == null)
            return "";
        return obj.toString();
    }

    /**
     * 移除session 值
     *
     * @param name name
     * @author jiangzeyin
     */
    protected void removeSessionAttribute(String name) {
        getSession().removeAttribute(name);
    }

    /**
     * 设置session 字符串
     *
     * @param name   name
     * @param object 值
     */
    protected void setSessionAttribute(String name, Object object) {
        getSession().setAttribute(name, object);
    }

    protected String getCookieValue(String name) {
        Cookie cookie = RequestUtil.getCookieByName(getRequest(), name);
        if (cookie == null)
            return "";
        return cookie.getValue();
    }

    protected String getParameter(String name) {
        return getParameter(name, null);
    }

    protected String[] getParameters(String name) {
        return getRequest().getParameterValues(name);
    }


    protected String getParameter(String name, String def) {
        String value = getRequest().getParameter(name);
        return value == null ? def : value;
    }

    protected int getParameterInt(String name, int def) {
        return StringUtil.parseInt(getParameter(name), def);
    }

    protected int getParameterInt(String name) {
        return getParameterInt(name, 0);
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
     * @throws IllegalAccessException y
     * @throws InstantiationException y
     */
    protected <T> T getObject(Class<T> tClass) throws IllegalAccessException, InstantiationException {
        Object obj = tClass.newInstance();
        doParameterMap(getRequest().getParameterMap(), obj);
        return (T) obj;
    }

    /**
     * 将map 赋值到对象属性中
     *
     * @param parameter parameter
     * @param obj       obj
     */
    void doParameterMap(Map<String, String[]> parameter, Object obj) {
        Iterator<Map.Entry<String, String[]>> entries = parameter.entrySet().iterator();
        Class tClass = obj.getClass();
        while (entries.hasNext()) {
            Map.Entry<String, String[]> entry = entries.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            String[] temp = (String[]) value;
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < temp.length; i++) {
                if (i != 0)
                    stringBuffer.append(",");
                stringBuffer.append(temp[i]);
            }
            setValue(tClass, obj, key, stringBuffer.toString());
        }
    }


    private void setValue(Class tClass, Object obj, String name, String value) {
        //Class tClass = obj.getClass();
        Field[] fields = tClass.getDeclaredFields();
        Class type = null;
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                type = field.getType();
                break;
            }
        }
        if (type == null) {
            Class superClass = tClass.getSuperclass();
            if (superClass != Object.class) {
                setValue(superClass, obj, name, value);
            }
            return;
        }
        try {
            Method method = getMethod(tClass, name, type);
            if (type == int.class || type == Integer.class) {
                try {
                    Integer integer = Integer.valueOf(value);
                    method.invoke(obj, integer);
                } catch (NumberFormatException ignored) {
                }
            } else if (type == String.class) {
                method.invoke(obj, value);
            } else if (AbstractBaseControl.class.isAssignableFrom(type)) {
                Object type_obj = type.newInstance();
                Method setIdMethod = getMethod(type_obj.getClass(), "Id", Integer.class);//type.getDeclaredMethod();
                try {
                    setIdMethod.invoke(type_obj, Integer.valueOf(value));
                    method.invoke(obj, type_obj);
                } catch (NumberFormatException ignored) {
                }
            } else if (type == Double.class || type == double.class) {
                try {
                    Double double_v = Double.valueOf(value);
                    method.invoke(obj, double_v);
                } catch (NumberFormatException ignored) {
                }
            } else {
                DefaultSystemLog.ERROR().error("没有设置:" + type, new RuntimeException());
            }
            System.out.println(type + "  " + name + "  " + value);
        } catch (Exception e) {
            DefaultSystemLog.ERROR().error("创建对象错误", e);
        }
    }

    private static Method getMethod(Class<?> tClass, String name, Class type) throws NoSuchMethodException {
        try {
            return tClass.getDeclaredMethod(parSetName(name), type);
        } catch (NoSuchMethodException e) {
            Class superClass = tClass.getSuperclass();
            if (superClass != Object.class)
                return getMethod(superClass, name, type);
            else
                throw e;
        }
    }

    private static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    private static String default_headerName;

    /**
     * 获取ip 地址
     *
     * @param request req
     * @return ip 信息
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (default_headerName == null)
            default_headerName = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.IP_DEFAULT_HEADER_NAME);
        String ipFromNginx = null;
        if (!StringUtil.isEmpty(default_headerName)) {
            ipFromNginx = request.getHeader(default_headerName);
        }
        if (ipFromNginx != null && ipFromNginx.length() > 0)
            return ipFromNginx;
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        ip = StringUtil.convertNULL(ip);
        return ip;
    }
}
