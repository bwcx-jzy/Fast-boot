package cn.jiangzeyin.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.controller.base.AbstractController;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 公共的拦截器
 *
 * @author jiangzeyin
 * @date 2017/2/17.
 */
public abstract class BaseInterceptor extends HandlerInterceptorAdapter {
    /**
     * controller 方法对象
     */
    private static final ThreadLocal<BaseCallbackController> BASE_CALLBACK_CONTROLLER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 释放资源
     */
    protected void clearResources() {
        BASE_CALLBACK_CONTROLLER_THREAD_LOCAL.remove();
    }

    /**
     * 获取session
     *
     * @return session
     */
    public static HttpSession getSession() {
        return BaseCallbackController.getRequestAttributes().getRequest().getSession();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = getHandlerMethod(handler);
        if (handlerMethod == null) {
            return true;
        }
        Object object = handlerMethod.getBean();
        Class controlClass = object.getClass();
        //  controller
        if (BaseCallbackController.class.isAssignableFrom(controlClass)) {
            BASE_CALLBACK_CONTROLLER_THREAD_LOCAL.set((BaseCallbackController) object);
        }
        return preHandle(request, response, handlerMethod);
    }

    protected HandlerMethod getHandlerMethod(Object handler) {
        if (handler instanceof HandlerMethod) {
            return (HandlerMethod) handler;
        }
        return null;
    }

    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        return true;
    }

    /**
     * 第二次回调
     */
    protected void reload() {
        BaseCallbackController baseCallbackController = BASE_CALLBACK_CONTROLLER_THREAD_LOCAL.get();
        if (baseCallbackController != null) {
            baseCallbackController.resetInfo();
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (response.getStatus() >= HttpStatus.BAD_REQUEST.value()) {
            //javax.servlet.error.exception"
            DefaultSystemLog.getLog().error("request code error:" + request.getRequestURI() + "  " + response.getStatus());
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        this.afterCompletion(request, ex);
        // 释放资源
        AbstractController.clearResources();
        clearResources();
    }

    private void afterCompletion(HttpServletRequest request, Exception ex) {
        if (ex == null) {
            return;
        }
        if (ex instanceof ClientAbortException) {
            ClientAbortException abortException = (ClientAbortException) ex;
            String message = abortException.getMessage();
            if (StrUtil.contains(message, "Broken pipe")) {
                DefaultSystemLog.getLog().warn("controller Exception:" + request.getRequestURL());
                return;
            }
        }
        DefaultSystemLog.getLog().error("controller Exception:" + request.getRequestURL(), ex);
    }
}
