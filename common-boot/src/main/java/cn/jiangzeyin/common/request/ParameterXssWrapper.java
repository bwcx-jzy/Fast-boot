package cn.jiangzeyin.common.request;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.common.DefaultSystemLog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
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
        //request = this;
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
     * @param map  map
     * @param utf8 utf8
     * @return 结果
     */
    public static Map<String, String[]> doXss(Map<String, String[]> map, boolean utf8) {
        Objects.requireNonNull(map);
        Iterator<Map.Entry<String, String[]>> iterator = map.entrySet().iterator();
        Map<String, String[]> valuesMap = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> entry = iterator.next();
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    if (!utf8)
                        values[i] = getUTF8(values[i]);
                    values[i] = StringUtil.filterHTML(values[i]);
                }
                valuesMap.put(key, values);
            }
        }
        return valuesMap;
    }

    public static String getUTF8(String str) {
        if (StringUtil.isEmpty(str))
            return "";
        try {
            return new String(str.getBytes("ISO-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            DefaultSystemLog.ERROR().error("iso-8859-1 to utf-8 失败", e);
            return "";
        }
    }

}
