package cn.jiangzeyin.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 每次进入controller 回调
 *
 * @author jiangzeyin
 * data 2018/6/26
 * @see Scope
 */
public abstract class BaseCallbackController {
    /**
     * 重置信息
     */
    public void resetInfo() {
    }

    /**
     * 全局获取请求对象
     *
     * @return req
     */
    public static ServletRequestAttributes getRequestAttributes() {
        ServletRequestAttributes servletRequestAttributes = tryGetRequestAttributes();
        Objects.requireNonNull(servletRequestAttributes);
        return servletRequestAttributes;
    }

    /**
     * 尝试获取
     *
     * @return ServletRequestAttributes
     */
    public static ServletRequestAttributes tryGetRequestAttributes() {
        RequestAttributes attributes = null;
        try {
            attributes = RequestContextHolder.currentRequestAttributes();
        } catch (IllegalStateException e) {
            // TODO: handle exception
        }
        if (attributes == null) {
            return null;
        }
        if (attributes instanceof ServletRequestAttributes) {
            return (ServletRequestAttributes) attributes;
        }
        return null;
    }

    /**
     * 获取客户端的ip地址
     *
     * @return 如果没有就返回null
     */
    public static String getClientIP() {
        ServletRequestAttributes servletRequest = tryGetRequestAttributes();
        if (servletRequest == null) {
            return null;
        }
        HttpServletRequest request = servletRequest.getRequest();
        if (request == null) {
            return null;
        }
        return ServletUtil.getClientIP(request);
    }

    /**
     * 获取header
     *
     * @param request req
     * @return map
     * @author jiangzeyin
     */
    public static Map<String, String> getHeaderMapValues(HttpServletRequest request) {
        Enumeration<String> enumeration = request.getHeaderNames();
        Map<String, String> headerMapValues = new HashMap<>(20);
        if (enumeration != null) {
            for (; enumeration.hasMoreElements(); ) {
                String name = enumeration.nextElement();
                headerMapValues.put(name, request.getHeader(name));
            }
        }
        return headerMapValues;
    }

    public HttpServletRequest getRequest() {
        HttpServletRequest request = getRequestAttributes().getRequest();
        Objects.requireNonNull(request, "request null");
        return request;
    }

    public HttpServletResponse getResponse() {
        HttpServletResponse response = getRequestAttributes().getResponse();
        Objects.requireNonNull(response, "response null");
        return response;
    }

    /**
     * 获取session
     *
     * @return session
     */
    public HttpSession getSession() {
        HttpSession session = getRequestAttributes().getRequest().getSession();
        if (session == null) {
            session = BaseInterceptor.getSession();
        }
        Objects.requireNonNull(session, "session null");
        return session;
    }

    /**
     * 获取Application
     *
     * @return Application
     */
    public ServletContext getApplication() {
        return getRequest().getServletContext();
    }

    public Object getAttribute(String name) {
        return getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    public void setAttribute(String name, Object object) {
        getRequestAttributes().setAttribute(name, object, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 获取session 字符串
     *
     * @param name name
     * @return str
     * @author jiangzeyin
     */
    public String getSessionAttribute(String name) {
        return Objects.toString(getSessionAttributeObj(name), "");
    }

    /**
     * 获取session 中对象
     *
     * @param name name
     * @return obj
     */
    public Object getSessionAttributeObj(String name) {
        return getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 移除session 值
     *
     * @param name name
     * @author jiangzeyin
     */
    public void removeSessionAttribute(String name) {
        getRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 设置session 字符串
     *
     * @param name   name
     * @param object 值
     */
    public void setSessionAttribute(String name, Object object) {
        getRequestAttributes().setAttribute(name, object, RequestAttributes.SCOPE_SESSION);
    }
}
