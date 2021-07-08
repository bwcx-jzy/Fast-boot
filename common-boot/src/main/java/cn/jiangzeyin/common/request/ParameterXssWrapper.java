package cn.jiangzeyin.common.request;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.spring.SpringUtil;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpMethod;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

/**
 * xss 注入拦截
 *
 * @author jiangzeyin
 * @date 2017/2/4.
 */
public class ParameterXssWrapper extends HttpServletRequestWrapper {
    private final Map<String, String[]> parameters;

    private final ServletInputStream inputStream;
    private final byte[] body;

    /**
     * @see CommonPropertiesFinal#REQUEST_COPY_INPUT_STREAM
     */
    private static Boolean copy;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     */
    ParameterXssWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 获取请求头编码
        Charset charset;
        if (HttpMethod.GET.name().equals(request.getMethod())) {
            charset = CharsetUtil.CHARSET_UTF_8;
        } else {
            charset = getCharset(request);
        }
        this.parameters = XssFilter.doXss(request.getParameterMap(), charset);
        // copy inputStream
        if (this.readCopyConfig()) {
            this.inputStream = request.getInputStream();
            this.body = IoUtil.readBytes(inputStream);
        } else {
            this.inputStream = null;
            this.body = null;
        }
    }

    private boolean readCopyConfig() {
        if (copy != null) {
            return copy;
        }
        try {
            copy = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.REQUEST_COPY_INPUT_STREAM, Boolean.class, false);
        } catch (ConversionFailedException ignored) {
            copy = false;
        }
        return copy;
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

    @Override
    public BufferedReader getReader() throws IOException {
        if (copy) {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }
        return super.getReader();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!copy) {
            return super.getInputStream();
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public int available() throws IOException {
                return byteArrayInputStream.available();
            }

            @Override
            public boolean isFinished() {
                return inputStream.isFinished();
            }

            @Override
            public boolean isReady() {
                return inputStream.isReady();
            }

            @Override
            public void setReadListener(ReadListener listener) {
                inputStream.setReadListener(listener);
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public int read(byte[] b, int off, int len) {
                return byteArrayInputStream.read(b, off, len);
            }
        };
    }
}
