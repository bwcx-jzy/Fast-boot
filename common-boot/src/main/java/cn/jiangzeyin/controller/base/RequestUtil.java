package cn.jiangzeyin.controller.base;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求工具类
 *
 * @author jiangzeyin
 */
public final class RequestUtil {

    /**
     * @param request req
     * @param name    name
     * @return cookie
     * @author jiangzeyin
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = readCookieMap(request);
        return cookieMap.getOrDefault(name, null);
    }

    /**
     * @param request req
     * @return map
     * @author jiangzeyin
     */
    private static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

    /**
     * 获取headr
     *
     * @param request req
     * @return map
     * @author jiangzeyin
     */
    public static Map<String, String> getHeaderMapValues(HttpServletRequest request) {
        Enumeration<String> enumeration = request.getHeaderNames();
        Map<String, String> headerMapValues = new HashMap<>();
        if (enumeration != null)
            for (; enumeration.hasMoreElements(); ) {
                String name = enumeration.nextElement();
                headerMapValues.put(name, request.getHeader(name));
            }
        return headerMapValues;
    }
}
