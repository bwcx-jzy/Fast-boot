package cn.jiangzeyin.common.request;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

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
        // 获取请求头编码
        Charset charset;
        if (HttpMethod.GET.name().equals(request.getMethod())) {
            charset = CharsetUtil.CHARSET_UTF_8;
        } else {
            charset = getCharset(request);
        }
        this.parameters = XssFilter.doXss(request.getParameterMap(), charset);
    }

    static Charset getCharset(HttpServletRequest request) {
        String contentType = request.getContentType();
        String charsetName = ReUtil.get(HttpUtil.CHARSET_PATTERN, contentType, 1);
        Charset charset = null;
        if (StrUtil.isNotBlank(charsetName)) {
            try {
                charset = Charset.forName(charsetName);
            } catch (UnsupportedCharsetException ignored) {
            }
        }
        return charset;
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
        return ArrayUtil.join(values, StrUtil.COMMA);
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }
}
