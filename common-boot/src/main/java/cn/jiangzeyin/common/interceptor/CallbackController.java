package cn.jiangzeyin.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 每次进入controller 回调
 * Created by jiangzeyin on 2018/6/26.
 */
public interface CallbackController {
    /**
     * 重置信息
     *
     * @param request  req
     * @param session  ses
     * @param response resp
     */
    void resetInfo(HttpServletRequest request, HttpSession session, HttpServletResponse response);

    /**
     * 二次回调，一般在拦截器中处理后，回调
     */
    void reload();
}
