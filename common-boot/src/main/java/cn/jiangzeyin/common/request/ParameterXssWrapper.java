package cn.jiangzeyin.common.request;

import cn.hutool.http.HtmlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * xss 注入拦截
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/4.
 */
public class ParameterXssWrapper extends HttpServletRequestWrapper {
    private final Map<String, String[]> parameters;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     */
    ParameterXssWrapper(HttpServletRequest request) {
        super(request);
        this.parameters = doXss(request.getParameterMap());
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
        String[] values = getParameterValues(name);
        if (values == null) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    /**
     * 处理xss 问题
     *
     * @param map map
     * @return 结果
     */
    public static Map<String, String[]> doXss(Map<String, String[]> map) {
        Objects.requireNonNull(map);
        Iterator<Map.Entry<String, String[]>> iterator = map.entrySet().iterator();
        Map<String, String[]> valuesMap = new HashMap<>(20);
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> entry = iterator.next();
            String key = entry.getKey();
            String[] values = entry.getValue();
            values = doXss(values);
            if (values != null) {
                valuesMap.put(key, values);
            }
        }
        return valuesMap;
    }

    private static String[] doXss(String[] values) {
        if (values == null) {
            return null;
        }
        for (int i = 0, len = values.length; i < len; i++) {
            values[i] = HtmlUtil.escape(values[i]);
        }
        return values;
    }
}
