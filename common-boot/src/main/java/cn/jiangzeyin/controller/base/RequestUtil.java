package cn.jiangzeyin.controller.base;

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
}
