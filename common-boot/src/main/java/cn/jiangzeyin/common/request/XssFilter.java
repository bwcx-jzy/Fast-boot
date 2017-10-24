package cn.jiangzeyin.common.request;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.spring.SpringUtil;
import cn.jiangzeyin.system.log.SystemLog;
import cn.jiangzeyin.util.net.http.RequestUtil;
import cn.jiangzeyin.util.util.StringUtil;
import org.apache.http.HttpStatus;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * xss 拦截器
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/5.
 */
public class XssFilter extends CharacterEncodingFilter {

    private static final ThreadLocal<Long> REQUEST_TIME = new ThreadLocal<>();
    private static final ThreadLocal<StringBuffer> REQUEST_INFO = new ThreadLocal<>();
    private static long request_timeout_log = -1;

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
    }

    /**
     * 记录请求信息
     *
     * @param request req
     */
    private void requestLog(HttpServletRequest request, boolean isFile) {
        // 获取请求信息
        Map<String, String> header = RequestUtil.getHeaderMapValues(request);
        Map<String, String[]> parameters = request.getParameterMap();
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
                        if (i != 0)
                            stringBuffer.append(",");
                        stringBuffer.append(isFile ? StringUtil.getUTF8(value[i]) : value[i]);
                    }
                }
                stringBuffer.append(";");
            }
            stringBuffer.append("}");
        } else {
            stringBuffer.append("null");
        }
        stringBuffer.append(",header:").append(header);
        SystemLog.LOG(SystemLog.LogType.REQUEST).info(stringBuffer.toString());
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
        if (status != HttpStatus.SC_OK) {
            String stringBuffer = "status:" +
                    status +
                    ",url:" +
                    REQUEST_INFO.get();
            SystemLog.LOG(SystemLog.LogType.REQUEST_ERROR).error(stringBuffer);
            return;
        }
        // 记录请求超时
        Long time = System.currentTimeMillis() - REQUEST_TIME.get();
        if (request_timeout_log == -1) {
            request_timeout_log = StringUtil.parseLong(SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.REQUEST_TIME_OUT, "3000"));
            if (request_timeout_log <= 0)
                request_timeout_log = 0;
        }
        if (request_timeout_log > 0 && time > request_timeout_log) {
            String stringBuffer = "time:" +
                    time +
                    ",url:" +
                    REQUEST_INFO.get();
            SystemLog.LOG(SystemLog.LogType.REQUEST_ERROR).error(stringBuffer);
        }
    }
}
