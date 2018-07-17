package cn.jiangzeyin.controller.base;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.interceptor.CallbackController;
import cn.jiangzeyin.common.spring.SpringUtil;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

/**
 * base
 * 公共的获取参数
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/12.
 */
public abstract class AbstractBaseControl extends CallbackController {
    //private static final ThreadLocal<HttpServletRequest> HTTP_SERVLET_REQUEST_THREAD_LOCAL = new ThreadLocal<>();
    //private static final ThreadLocal<HttpSession> HTTP_SESSION_THREAD_LOCAL = new ThreadLocal<>();
    //private static final ThreadLocal<HttpServletResponse> HTTP_SERVLET_RESPONSE_THREAD_LOCAL = new ThreadLocal<>();
    /**
     * ip 地址
     */
    private String ip;


    /**
     * 拦截器注入
     */
    @Override
    public void resetInfo() {
        super.resetInfo();
        //HTTP_SERVLET_REQUEST_THREAD_LOCAL.set(request);
        //HTTP_SESSION_THREAD_LOCAL.set(session);
        //HTTP_SERVLET_RESPONSE_THREAD_LOCAL.set(response);
        //this.ip = getIpAddress(request);
        //response.setCharacterEncoding("UTF-8");
    }

    /**
     * 获取请求的ip 地址
     *
     * @return ip
     */
    protected String getIp() {
        if (StringUtil.isEmpty(ip)) {
            this.ip = getIpAddress(getRequest());
        }
        return this.ip;
    }

    protected String getHeader(String name) {
        return getRequest().getHeader(name);
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
        return StringUtil.parseInt(getParameter(name), def);
    }

    protected int getParameterInt(String name) {
        return getParameterInt(name, 0);
    }

    protected long getParameterLong(String name, long def) {
        String value = getParameter(name);
        if (value == null)
            return def;
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
                method.invoke(obj, Integer.valueOf(value));
            } else if (type == long.class || type == Long.class) {
                method.invoke(obj, Long.valueOf(value));
            } else if (type == String.class) {
                method.invoke(obj, value);
            } else if (type == BigDecimal.class) {
                method.invoke(obj, BigDecimal.valueOf(Long.parseLong(value)));
            } else if (type == float.class || type == Float.class) {
                method.invoke(obj, Float.valueOf(value));
            } else if (AbstractBaseControl.class.isAssignableFrom(type)) {
                Object type_obj = type.newInstance();
                Method setIdMethod = getMethod(type_obj.getClass(), "Id", Integer.class);//type.getDeclaredMethod();
                try {
                    setIdMethod.invoke(type_obj, Integer.valueOf(value));
                    method.invoke(obj, type_obj);
                } catch (NumberFormatException ignored) {
                }
            } else if (type == Double.class || type == double.class) {
                method.invoke(obj, Double.valueOf(value));
            } else {
                DefaultSystemLog.ERROR().error("未设置对应数据类型:" + type, new RuntimeException());
            }
            //System.out.println(type + "  " + name + "  " + value);
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
