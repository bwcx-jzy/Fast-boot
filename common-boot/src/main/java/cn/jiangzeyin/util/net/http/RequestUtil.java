package cn.jiangzeyin.util.net.http;

import cn.jiangzeyin.util.util.StringUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     * 获取ip地址
     *
     * @param request req
     * @return str
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipFromNginx = request.getHeader("X-Real-IP");
        if (!StringUtil.isEmpty(ipFromNginx))
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
        // if (ip.startsWith("192.168.") || ip.startsWith("10.") ||
        // ip.startsWith("172.16"))
        // return "";
        return ip;
    }

    /**
     * @param request r
     * @return s
     * @author jiangzeyin
     */
    public static String getIpInfo(HttpServletRequest request) {
        StringBuilder ipInfo = new StringBuilder();
        String ipFromNginx = request.getHeader("X-Real-IP");
        if (!StringUtil.isEmpty(ipFromNginx)) {
            ipInfo.append("X-Real-IP:").append(ipFromNginx);
        }
        // ipInfo.append("ipFromNginx:" + ipFromNginx);
        String ip = request.getHeader("x-forwarded-for");
        ipInfo.append("x-forwarded-for:" + ip);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            ipInfo.append("Proxy-Client-IP:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            ipInfo.append("WL-Proxy-Client-IP:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            ipInfo.append("HTTP_CLIENT_IP:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            ipInfo.append("HTTP_X_FORWARDED_FOR:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            ipInfo.append("Addr:" + ip);
        }
        ipInfo.append(ip);
        return ipInfo.toString();
    }

    /**
     * @param agent agent
     * @return str
     * @author jiangzeyin
     */
    public String getBrowserName(String agent) {
        if (agent.indexOf("msie 7") > 0) {
            return "ie7";
        } else if (agent.indexOf("msie 8") > 0) {
            return "ie8";
        } else if (agent.indexOf("msie 9") > 0) {
            return "ie9";
        } else if (agent.indexOf("msie 10") > 0) {
            return "ie10";
        } else if (agent.indexOf("msie") > 0) {
            return "ie";
        } else if (agent.indexOf("opera") > 0) {
            return "opera";
        } else if (agent.indexOf("opera") > 0) {
            return "opera";
        } else if (agent.indexOf("firefox") > 0) {
            return "firefox";
        } else if (agent.indexOf("webkit") > 0) {
            return "webkit";
        } else if (agent.indexOf("gecko") > 0 && agent.indexOf("rv:11") > 0) {
            return "ie11";
        } else {
            return "Others";
        }
    }

    // public static String getIp2(HttpServletRequest request) {
    // String ip = request.getHeader("X-Forwarded-For");
    // if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
    // // 多次反向代理后会有多个ip值，第一个ip才是真实ip
    // int index = ip.indexOf(",");
    // if (index != -1) {
    // return ip.substring(0, index);
    // } else {
    // return ip;
    // }
    // }
    // ip = request.getHeader("X-Real-IP");
    // if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
    // return ip;
    // }
    // return request.getRemoteAddr();
    // }

    /**
     * @param response r
     * @param name     n
     * @param value    v
     * @param maxAge   age
     * @author jiangzeyin
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("../");
        if (maxAge > 0)
            cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 删除cookie
     *
     * @param request  req
     * @param response rep
     * @param name     name
     * @author jiangzeyin
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue(null);
                    cookie.setMaxAge(0);// 立即销毁cookie
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }

    /**
     * @param request req
     * @param name    name
     * @return cookie
     * @author jiangzeyin
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = ReadCookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }

    /**
     * @param request req
     * @return map
     * @author jiangzeyin
     */
    private static Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {
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
     * @param request req
     * @return s
     * @author jiangzeyin
     */
    public static Map<String, String> getCookieMapValues(HttpServletRequest request) {
        Map<String, String> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie.getValue());
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
