package cn.jiangzeyin.common.request;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.spring.SpringUtil;
import cn.jiangzeyin.controller.base.RequestUtil;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    public static Map<String, String> getRequestHeader() {
        return REQUEST_HEADER_MAP.get();
    }

    public static Map<String, String[]> getRequestParameters() {
        return REQUEST_PARAMETERS_MAP.get();
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
        boolean isFile = ServletFileUpload.isMultipartContent(request);
        if (!isFile) {
            request = new ParameterXssWrapper(request);
        }
        requestLog(request, isFile);
        super.doFilterInternal(request, response, filterChain);
        responseLog(response);
        cleanThreadLocal();
    }

    /**
     * 记录请求信息
     *
     * @param request req
     */
    private void requestLog(HttpServletRequest request, boolean isFile) {
        // 获取请求信息
        Map<String, String> header = RequestUtil.getHeaderMapValues(request);
        REQUEST_HEADER_MAP.set(header);
        Map<String, String[]> parameters = request.getParameterMap();
        REQUEST_PARAMETERS_MAP.set(parameters);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(request.getRequestURI());
        //.append(",ip:").append(RequestUtil.getIpAddress(request))
        stringBuffer.append(" parameters:");
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
                        stringBuffer.append(isFile ? ParameterXssWrapper.getUTF8(value[i]) : value[i]);
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
            request_timeout_log = StringUtil.parseLong(SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.REQUEST_TIME_OUT, "3000"));
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
}
