package cn.jiangzeyin.common.interceptor;

import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.controller.base.AbstractBaseControl;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 公共的拦截器
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/17.
 */
public abstract class BaseInterceptor extends HandlerInterceptorAdapter {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;
    protected ServletContext application;
    protected String url;
    private BaseCallbackController baseCallbackController;


    private static final ThreadLocal<HttpSession> HTTP_SESSION_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 释放资源
     */
    protected void clearResources() {
        HTTP_SESSION_THREAD_LOCAL.remove();
    }

    /**
     * 获取session
     *
     * @return session
     */
    public static HttpSession getSession() {
        return HTTP_SESSION_THREAD_LOCAL.get();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.request = request;
        this.response = response;
        this.session = request.getSession();
        this.application = session.getServletContext();
        HTTP_SESSION_THREAD_LOCAL.set(this.session);
        this.url = request.getRequestURI();
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Object object = handlerMethod.getBean();
            Class controlClass = object.getClass();
            //  controller
            if (BaseCallbackController.class.isAssignableFrom(controlClass)) {
                // baseCallbackController.resetInfo(this.request, this.session, this.response);
                this.baseCallbackController = (BaseCallbackController) object;
            }
        }
        return true;
    }

    /**
     * 第二次回调
     */
    protected void reload() {
        if (baseCallbackController != null) {
            baseCallbackController.resetInfo();
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (response.getStatus() != HttpStatus.OK.value()) {
            DefaultSystemLog.LOG().info("请求错误:" + request.getRequestURI() + "  " + response.getStatus());
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            DefaultSystemLog.ERROR().error("controller 异常:" + request.getRequestURL(), ex);
        }
        // 释放资源
        AbstractBaseControl.clearResources();
        clearResources();
    }
}
