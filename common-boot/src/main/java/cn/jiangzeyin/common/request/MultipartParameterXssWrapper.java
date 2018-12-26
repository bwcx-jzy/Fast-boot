package cn.jiangzeyin.common.request;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map;

/**
 * 文件上传拦截器
 * Created by jiangzeyin on 2018/8/21.
 *
 * @author jiangzeyin
 */
public class MultipartParameterXssWrapper extends StandardMultipartHttpServletRequest {

    private final Map<String, String[]> parameters;


    public MultipartParameterXssWrapper(HttpServletRequest request) throws MultipartException {
        super(request);
        // 获取请求头编码
        Charset charset = ParameterXssWrapper.getCharset(request);
        parameters = XssFilter.doXss(super.getParameterMap(), charset);
    }


    @Override
    public Enumeration<String> getParameterNames() {
        return super.getParameterNames();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public String getParameter(String name) {
        String[] values = getParameterValues(name);
        if (values == null) {
            return null;
        }
        return ArrayUtil.join(values, StrUtil.COMMA);
    }

    @Override
    public String[] getParameterValues(String name) {
        if (parameters == null) {
            return null;
        }
        return parameters.get(name);
    }
}
