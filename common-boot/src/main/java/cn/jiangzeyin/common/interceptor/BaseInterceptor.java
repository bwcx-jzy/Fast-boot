package cn.jiangzeyin.common.interceptor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.controller.base.AbstractController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
            DefaultSystemLog.getLog().error("请求错误:" + request.getRequestURI() + "  " + response.getStatus());
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            DefaultSystemLog.getLog().error("controller 异常:" + request.getRequestURL(), ex);
        }
        // 释放资源
        AbstractController.clearResources();
        clearResources();
    }

    /**
     * 获取 protocol 协议完全跳转
     *
     * @param request  请求
     * @param response 响应
     * @param url      跳转url
     * @throws IOException io
     */
    public static void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String proto = ServletUtil.getHeaderIgnoreCase(request, "X-Forwarded-Proto");
        if (proto == null) {
            response.sendRedirect(url);
        } else {
            String host = request.getHeader(HttpHeaders.HOST);
            if (StrUtil.isEmpty(host)) {
                throw new RuntimeException("请配置host header");
            }
            int remotePort = request.getRemotePort();
            String remotePortStr = "";
//            if (remotePort != 80 && remotePort != 443) {
//                remotePortStr = ":" + String.valueOf(remotePort);
//            }
            //javax.servlet.http.HttpUtils.getRequestURL
            String toUrl = StrUtil.format("{}://{}{}{}", proto, host, remotePortStr, url);
            response.sendRedirect(toUrl);
        }
    }

    /**
     * 二级代理路径
     *
     * @param request req
     * @return 请求头配置 + context-path
     */
    public static String getHeaderProxyPath(HttpServletRequest request, String headerName) {
        String proxyPath = ServletUtil.getHeaderIgnoreCase(request, headerName);
        if (StrUtil.isEmpty(proxyPath)) {
            return request.getContextPath();
        }
        proxyPath = FileUtil.normalize(proxyPath + StrUtil.SLASH + request.getContextPath());
        if (proxyPath.endsWith(StrUtil.SLASH)) {
            proxyPath = proxyPath.substring(0, proxyPath.length() - 1);
        }
        return proxyPath;
    }
}
