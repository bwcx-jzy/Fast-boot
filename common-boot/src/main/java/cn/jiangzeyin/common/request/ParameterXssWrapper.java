package cn.jiangzeyin.common.request;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * xss 注入拦截
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/4.
 */
public class ParameterXssWrapper extends HttpServletRequestWrapper {
    private final Map<String, String[]> parameters;
    private final HttpServletRequest request;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     */
    ParameterXssWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
        this.parameters = doXss(request.getParameterMap(), true);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector<>(parameters.keySet()).elements();
    }

    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    /**
     * 处理xss 问题
     *
     * @param map         map
     * @param convertUtf8 utf8
     * @return 结果
     */
    public static Map<String, String[]> doXss(Map<String, String[]> map, boolean convertUtf8) {
        Objects.requireNonNull(map);
        Iterator<Map.Entry<String, String[]>> iterator = map.entrySet().iterator();
        Map<String, String[]> valuesMap = new HashMap<>(20);
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> entry = iterator.next();
            String key = entry.getKey();
            String[] values = entry.getValue();
            values = doXss(values, convertUtf8);
            if (values != null) {
                valuesMap.put(key, values);
            }
        }
        return valuesMap;
    }

    private static String[] doXss(String[] values, boolean convertUtf8) {
        if (values == null) {
            return null;
        }
        for (int i = 0, len = values.length; i < len; i++) {
            if (!convertUtf8) {
                values[i] = getUTF8(values[i]);
            }
            values[i] = HtmlUtil.escape(values[i]);
        }
        return values;
    }

    static String getUTF8(String str) {
        if (StrUtil.isEmpty(str)) {
            return "";
        }
        return new String(str.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
