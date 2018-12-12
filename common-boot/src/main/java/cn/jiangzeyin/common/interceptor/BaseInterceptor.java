package cn.jiangzeyin.common.interceptor;

import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.controller.base.AbstractController;
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
 * Created by jiangzeyin on 2017/2/17.
 */
public abstract class BaseInterceptor extends HandlerInterceptorAdapter {
    /**
     * controller 方法对象
     */
    private static final ThreadLocal<HandlerMethod> HANDLER_METHOD_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<BaseCallbackController> BASE_CALLBACK_CONTROLLER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<HttpSession> HTTP_SESSION_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 释放资源
     */
    protected void clearResources() {
        HTTP_SESSION_THREAD_LOCAL.remove();
        HANDLER_METHOD_THREAD_LOCAL.remove();
        BASE_CALLBACK_CONTROLLER_THREAD_LOCAL.remove();
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
        HTTP_SESSION_THREAD_LOCAL.set(request.getSession());
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            HANDLER_METHOD_THREAD_LOCAL.set(handlerMethod);
            Object object = handlerMethod.getBean();
            Class controlClass = object.getClass();
            //  controller
            if (BaseCallbackController.class.isAssignableFrom(controlClass)) {
                BASE_CALLBACK_CONTROLLER_THREAD_LOCAL.set((BaseCallbackController) object);
            }
        }
        return true;
    }

    protected HandlerMethod getHandlerMethod() {
        return HANDLER_METHOD_THREAD_LOCAL.get();
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
        AbstractController.clearResources();
        clearResources();
    }
}
