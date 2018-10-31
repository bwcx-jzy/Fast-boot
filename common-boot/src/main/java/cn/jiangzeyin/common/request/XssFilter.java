package cn.jiangzeyin.common.request;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HtmlUtil;
import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.interceptor.BaseCallbackController;
import cn.jiangzeyin.common.spring.SpringUtil;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * xss 过滤器
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/5.
 */
public class XssFilter extends CharacterEncodingFilter {

    private static final ThreadLocal<Long> REQUEST_TIME = new ThreadLocal<>();
    private static final ThreadLocal<StringBuffer> REQUEST_INFO = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> REQUEST_HEADER_MAP = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String[]>> REQUEST_PARAMETERS_MAP = new ThreadLocal<>();
    private static long request_timeout_log = -1;
    /**
     * 默认true 配置错误 false
     */
    private static boolean LOG;
    /**
     * 默认true 配置错误 true
     */
    private static boolean XSS;

    static {
        // 日志标记
        try {
            LOG = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.REQUEST_LOG, Boolean.class, true);
        } catch (ConversionFailedException ignored) {
            LOG = false;
        }
        // xss 标记
        try {
            XSS = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.REQUEST_PARAMETER_XSS, Boolean.class, true);
        } catch (ConversionFailedException ignored) {
            XSS = true;
        }
    }

    /**
     * 释放资源
     */
    private static void cleanThreadLocal() {
        REQUEST_HEADER_MAP.remove();
        REQUEST_INFO.remove();
        REQUEST_TIME.remove();
        REQUEST_PARAMETERS_MAP.remove();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        REQUEST_TIME.set(System.currentTimeMillis());
        File location = ((File) request.getServletContext().getAttribute(ServletContext.TEMPDIR));
        if (!location.exists() && !location.mkdirs()) {
            throw new IOException(location.getPath() + " 临时目录创建失败");
        }
        boolean isFile = ServletFileUpload.isMultipartContent(request);
        HttpServletRequest newRequest;
        if (isFile) {
            newRequest = new MultipartParameterXssWrapper(request);
        } else {
            newRequest = new ParameterXssWrapper(request);
        }
        requestLog(newRequest);
        super.doFilterInternal(newRequest, response, filterChain);
        responseLog(response);
        cleanThreadLocal();
    }

    /**
     * 记录请求信息
     *
     * @param request req
     */
    private void requestLog(HttpServletRequest request) {
        if (!LOG) {
            return;
        }
        // 获取请求信息
        Map<String, String> header = BaseCallbackController.getHeaderMapValues(request);
        REQUEST_HEADER_MAP.set(header);
        Map<String, String[]> parameters = request.getParameterMap();
        REQUEST_PARAMETERS_MAP.set(parameters);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(request.getRequestURI())
                .append(",ip:").append(ServletUtil.getClientIP(request))
                .append(" parameters:");
        if (parameters != null) {
            Set<Map.Entry<String, String[]>> entries = parameters.entrySet();
            stringBuffer.append("{");
            for (Map.Entry<String, String[]> entry : entries) {
                String key = entry.getKey();
                stringBuffer.append(key).append(":");
                String[] value = entry.getValue();
                if (value != null) {
                    for (int i = 0; i < value.length; i++) {
                        if (i != 0) {
                            stringBuffer.append(",");
                        }
                        stringBuffer.append(HtmlUtil.unescape(value[i]));
                    }
                }
                stringBuffer.append(";");
            }
            stringBuffer.append("}");
        } else {
            stringBuffer.append("null");
        }
        stringBuffer.append(",header:").append(header);
        DefaultSystemLog.LOG(DefaultSystemLog.LogType.REQUEST).info(stringBuffer.toString());
        REQUEST_INFO.set(stringBuffer);
    }

    /**
     * 响应记录
     *
     * @param response rep
     */
    private void responseLog(HttpServletResponse response) {
        if (!LOG) {
            return;
        }
        // 记录请求状态不正确
        int status = response.getStatus();
        if (status != HttpStatus.OK.value() && status != HttpStatus.FOUND.value()) {
            String stringBuffer = "status:" +
                    status +
                    ",url:" +
                    REQUEST_INFO.get();
            DefaultSystemLog.LOG(DefaultSystemLog.LogType.REQUEST_ERROR).error(stringBuffer);
            return;
        }
        // 记录请求超时
        long time = System.currentTimeMillis() - REQUEST_TIME.get();
        if (request_timeout_log == -1) {
            Long timeOut = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.REQUEST_TIME_OUT, Long.class, 3000L);
            request_timeout_log = Convert.toLong(timeOut, 3000L);
            if (request_timeout_log <= 0) {
                request_timeout_log = 0;
            }
        }
        if (request_timeout_log > 0 && time > request_timeout_log) {
            String stringBuffer = "time:" +
                    time +
                    ",url:" +
                    REQUEST_INFO.get();
            DefaultSystemLog.LOG(DefaultSystemLog.LogType.REQUEST_ERROR).error(stringBuffer);
        }
    }

    /**
     * 处理xss 问题
     *
     * @param map map
     * @return 结果
     */
    static Map<String, String[]> doXss(Map<String, String[]> map) {
        if (null == map) {
            return null;
        }
        Iterator<Map.Entry<String, String[]>> iterator = map.entrySet().iterator();
        Map<String, String[]> valuesMap = new HashMap<>(map.size());
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
            if (null == values[i]) {
                continue;
            }
            // 自动处理utf-8
            values[i] = autoToUtf8(values[i]);
            if (XSS) {
                //  xss 提前统一编码
                values[i] = HtmlUtil.escape(values[i])
                        .replace(StrUtil.HTML_QUOTE, "\"");
            }
        }
        return values;
    }

    private static String autoToUtf8(String str) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        String newStr = CharsetUtil.convert(str, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8);
        if (str.length() >= newStr.length()) {
            return str;
        }
        return newStr;
    }
}
