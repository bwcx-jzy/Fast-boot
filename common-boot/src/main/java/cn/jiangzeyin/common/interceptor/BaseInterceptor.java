package cn.jiangzeyin.common.interceptor;

import cn.jiangzeyin.controller.base.AbstractBaseControl;
import cn.jiangzeyin.controller.base.AbstractMultipartFileBaseControl;
import cn.jiangzeyin.system.log.LogType;
import cn.jiangzeyin.system.log.SystemLog;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/17.
 */
public abstract class BaseInterceptor extends HandlerInterceptorAdapter {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;
    protected ServletContext application;
    protected String url;
    private AbstractBaseControl abstractBaseControl;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.session = request.getSession();
        this.application = session.getServletContext();
        this.request = request;
        this.response = response;
        this.url = request.getRequestURI();
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class controlClass = ((HandlerMethod) handler).getBean().getClass();
            Object object = handlerMethod.getBean();
            // 文件上传
            if (AbstractMultipartFileBaseControl.class.isAssignableFrom(controlClass)) {
                AbstractMultipartFileBaseControl abstractMultipartFileBaseControl = (AbstractMultipartFileBaseControl) object;
                abstractMultipartFileBaseControl.setReqAndRes(this.request, this.session, this.response);
                abstractBaseControl = abstractMultipartFileBaseControl;
            } else if (AbstractBaseControl.class.isAssignableFrom(controlClass)) {
                abstractBaseControl = (AbstractBaseControl) object;
                abstractBaseControl.setReqAndRes(this.request, this.session, this.response);
            }
        }
        return true;
    }

    protected void reload() {
        if (abstractBaseControl != null)
            abstractBaseControl.reLoad();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (response.getStatus() != HttpStatus.OK.value()) {
            SystemLog.LOG(LogType.CONTROL_ERROR).info("请求错误:" + request.getRequestURI() + "  " + response.getStatus());
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null)
            SystemLog.LOG(LogType.CONTROL_ERROR).error("controller 异常", ex);
    }
}
