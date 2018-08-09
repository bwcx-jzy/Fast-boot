package cn.jiangzeyin.common.interceptor;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * 每次进入controller 回调
 *
 * @author jiangzeyin
 * data 2018/6/26
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
        RequestAttributes attributes = null;
        try {
            attributes = RequestContextHolder.currentRequestAttributes();
        } catch (IllegalStateException e) {
            // TODO: handle exception
            DefaultSystemLog.ERROR().error("获取req失败", e);
        }
        Objects.requireNonNull(attributes);
        if (attributes instanceof ServletRequestAttributes) {
            return (ServletRequestAttributes) attributes;
        }
        throw new IllegalArgumentException("error");
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

    public HttpServletRequest getRequest() {
        HttpServletRequest request = getRequestAttributes().getRequest();
        Objects.requireNonNull(request, "request null");
        return request;
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
        return StringUtil.convertNULL(getSessionAttributeObj(name));
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
