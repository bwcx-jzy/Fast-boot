package cn.jiangzeyin.common.request;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * xss 过滤器
 *
 * @author jiangzeyin
 * @date 2017/1/5.
 */
public class XssFilter extends CharacterEncodingFilter {

    private static final ThreadLocal<Long> REQUEST_TIME = new ThreadLocal<>();
    private static final ThreadLocal<String> REQUEST_INFO = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> REQUEST_HEADER_MAP = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String[]>> REQUEST_PARAMETERS_MAP = new ThreadLocal<>();
    private static long request_timeout_log = 3000L;
    /**
     * 默认true 配置错误 false
     */
    private static boolean LOG;
    /**
     * 默认true 配置错误 true
     */
    private static boolean XSS;
    /**
     * 参数前后空格 默认false
     */
    private static boolean TRIMAll;
    /**
     * 控制台日志管理字段
     */
    public static String[] logFilterPar = new String[]{"pwd", "pass", "password"};
    /**
     *
     */
    private static String[] RESOURCE_HANDLER;

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
        // 参数空格
        try {
            TRIMAll = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.REQUEST_PARAMETER_TRIM_ALL, Boolean.class, false);
        } catch (ConversionFailedException ignored) {
            TRIMAll = false;
        }
        // 超时时间
        try {
            Long timeOut = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.REQUEST_TIME_OUT, Long.class, request_timeout_log);
            if (timeOut != null) {
                request_timeout_log = timeOut;
            }
        } catch (ConversionFailedException ignored) {
        }
        // 静态资源url
        String val = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.INTERCEPTOR_RESOURCE_HANDLER);
        if (StrUtil.isNotEmpty(val)) {
            RESOURCE_HANDLER = StrUtil.split(val, StrUtil.COMMA);
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
        String url = request.getRequestURI();
        // 静态资源部记录
        if (RESOURCE_HANDLER != null) {
            for (String item : RESOURCE_HANDLER) {
                if (StrUtil.endWith(item, "/**")) {
                    item = item.substring(0, item.length() - 2);
                }
                if (StrUtil.startWith(url, FileUtil.normalize(StrUtil.SLASH + item))) {
                    return;
                }
            }
        }
        // 获取请求信息
        Map<String, String> header = BaseCallbackController.getHeaderMapValues(request);
        REQUEST_HEADER_MAP.set(header);
        Map<String, String[]> parameters = request.getParameterMap();
        REQUEST_PARAMETERS_MAP.set(parameters);
        String ip = ServletUtil.getClientIP(request);
        DefaultSystemLog.LogCallback logCallback = DefaultSystemLog.getLogCallback();
        if (logCallback != null) {
            String id = IdUtil.fastSimpleUUID();
            logCallback.log(DefaultSystemLog.LogType.REQUEST, id, url, ip, parameters, header);
            REQUEST_INFO.set(id);
        } else {
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append(url)
                    .append(",ip:").append(ip)
                    .append(" parameters:");
            if (parameters != null) {
                Set<Map.Entry<String, String[]>> entries = parameters.entrySet();
                stringBuffer.append("{");
                for (Map.Entry<String, String[]> entry : entries) {
                    String key = entry.getKey();
                    if (StrUtil.containsAnyIgnoreCase(key, logFilterPar)) {
                        continue;
                    }
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
            DefaultSystemLog.getLog().info(stringBuffer.toString());
            REQUEST_INFO.set(stringBuffer.toString());
        }
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
        String urlInfo = REQUEST_INFO.get();
        if (urlInfo == null) {
            return;
        }
        // 记录请求状态不正确
        int status = response.getStatus();
        DefaultSystemLog.LogCallback logCallback = DefaultSystemLog.getLogCallback();
        if (status >= HttpStatus.BAD_REQUEST.value()) {
            if (logCallback != null) {
                logCallback.log(DefaultSystemLog.LogType.REQUEST_ERROR, "status", urlInfo, status);
            } else {
                String stringBuffer = "status:" +
                        status +
                        ",url:" +
                        urlInfo;
                DefaultSystemLog.getLog().error(stringBuffer);
            }
            return;
        }
        // 记录请求超时
        long time = System.currentTimeMillis() - REQUEST_TIME.get();
        if (request_timeout_log > 0 && time > request_timeout_log) {
            if (logCallback != null) {
                logCallback.log(DefaultSystemLog.LogType.REQUEST_ERROR, "time", urlInfo, time);
            } else {
                String stringBuffer = "time:" +
                        time +
                        ",url:" +
                        urlInfo;
                DefaultSystemLog.getLog().error(stringBuffer);
            }
        }
    }

    /**
     * 处理xss 问题
     *
     * @param map map
     * @return 结果
     */
    static Map<String, String[]> doXss(Map<String, String[]> map, Charset charset) {
        if (null == map) {
            return null;
        }
        Iterator<Map.Entry<String, String[]>> iterator = map.entrySet().iterator();
        Map<String, String[]> valuesMap = new HashMap<>(map.size());
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> entry = iterator.next();
            String key = entry.getKey();
            String[] values = entry.getValue();
            values = doXss(values, charset);
            if (values != null) {
                valuesMap.put(key, values);
            }
        }
        return valuesMap;
    }

    private static String[] doXss(String[] values, Charset charset) {
        if (values == null) {
            return null;
        }
        for (int i = 0, len = values.length; i < len; i++) {
            if (null == values[i]) {
                continue;
            }
            // 自动处理utf-8
            values[i] = autoToUtf8(values[i], charset);
            if (XSS) {
                //  xss 提前统一编码
                values[i] = xss(values[i]);
            }
            if (TRIMAll) {
                // 空格
                values[i] = values[i].trim();
            }
        }
        return values;
    }

    /**
     * xss标签过滤
     *
     * @param value 需要过滤的值
     * @return 过滤后的
     * @since 1.2.33
     */
    public static String xss(String value) {
        if (value == null) {
            return null;
        }
        //  xss 提前统一编码
        return HtmlUtil.escape(value)
                .replace(StrUtil.HTML_QUOTE, "\"");
    }

    private static String autoToUtf8(String str, Charset charset) {
        if (charset == CharsetUtil.CHARSET_UTF_8) {
            return str;
        }
        return CharsetUtil.convert(str, charset, StandardCharsets.UTF_8);
    }

    /**
     * 返回是否已经xss
     *
     * @return 配置的状态
     * @since 1.2.33
     */
    public static boolean isXSS() {
        return XSS;
    }
}
