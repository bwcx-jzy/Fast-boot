package cn.jiangzeyin.controller.base;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpUtil;
import cn.jiangzeyin.common.interceptor.BaseCallbackController;
import cn.jiangzeyin.controller.multipart.MultipartFileConfig;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * base
 * 公共的获取参数
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/12.
 */
public abstract class AbstractBaseControl extends BaseCallbackController {

    /**
     * 拦截器注入
     */
    @Override
    public void resetInfo() {
        super.resetInfo();
    }

    /**
     * 获取请求的ip 地址
     *
     * @return ip
     */
    protected String getIp() {
        return ServletUtil.getClientIP(getRequest());
    }

    protected String getHeader(String name) {
        return getRequest().getHeader(name);
    }

    /**
     * 获取cookie 值
     *
     * @param name name
     * @return value
     */
    protected String getCookieValue(String name) {
        Cookie cookie = ServletUtil.getCookie(getRequest(), name);
        if (cookie == null) {
            return "";
        }
        return cookie.getValue();
    }

    protected String getParameter(String name) {
        return getParameter(name, null);
    }

    protected String[] getParameters(String name) {
        return getRequest().getParameterValues(name);
    }

    /**
     * 获取指定参数名的值
     *
     * @param name 参数名
     * @param def  默认值
     * @return str
     */
    protected String getParameter(String name, String def) {
        String value = getRequest().getParameter(name);
        return value == null ? def : value;
    }

    protected int getParameterInt(String name, int def) {
        return Convert.toInt(getParameter(name), def);
    }

    protected int getParameterInt(String name) {
        return getParameterInt(name, 0);
    }

    protected long getParameterLong(String name, long def) {
        String value = getParameter(name);
        return Convert.toLong(value, def);
    }

    protected long getParameterLong(String name) {
        return getParameterLong(name, 0L);
    }

    /**
     * 获取来源的url 参数
     *
     * @return map
     */
    protected Map<String, String> getRefererParameter() {
        String referer = getHeader(HttpHeaders.REFERER);
        return HttpUtil.decodeParamMap(referer, "UTF-8");
    }

    /**
     * 获取表单数据到实体中
     *
     * @param tClass class
     * @param <T>    t
     * @return t
     */
    protected <T> T getObject(Class<T> tClass) {
        return ServletUtil.toBean(getRequest(), tClass, true);
    }

    /**
     * 获取所有请求头
     *
     * @return map
     */
    protected Map<String, String> getHeaders() {
        return RequestUtil.getHeaderMapValues(getRequest());
    }

    /**
     * 所有参数
     *
     * @return map 值为数组类型
     */
    protected Map<String, String[]> getParametersMap() {
        return getRequest().getParameterMap();
    }

    // ----------------文件上传

    private static final ThreadLocal<MultipartHttpServletRequest> THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST = new ThreadLocal<>();

    /**
     * 释放资源
     */
    public static void clearResources() {
        THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.remove();
    }

    protected MultipartHttpServletRequest getMultiRequest() {
        HttpServletRequest request = getRequest();
        if (request instanceof MultipartHttpServletRequest) {
            return (MultipartHttpServletRequest) request;
        }
        if (ServletFileUpload.isMultipartContent(request)) {
            MultipartHttpServletRequest multipartHttpServletRequest = THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.get();
            if (multipartHttpServletRequest != null) {
                return multipartHttpServletRequest;
            }
            multipartHttpServletRequest = new StandardMultipartHttpServletRequest(request);
            THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.set(multipartHttpServletRequest);
            return multipartHttpServletRequest;
        }
        throw new IllegalArgumentException("not MultipartHttpServletRequest");
    }

    protected MultipartFile getFile(String name) {
        return getMultiRequest().getFile(name);
    }

    protected List<MultipartFile> getFiles(String name) {
        return getMultiRequest().getFiles(name);
    }

    /**
     * 接收文件
     *
     * @param name 字段名称
     * @return 保存位置
     * @throws IOException IO
     */
    protected String upload(String name) throws IOException {
        return upload(new String[]{name})[0];
    }

    protected String[] upload(String... name) throws IOException {
        Objects.requireNonNull(name);
        String[] path = new String[name.length];
        String localPath = MultipartFileConfig.getFileTempPath();
        for (int i = 0, len = path.length; i < len; i++) {
            String item = name[i];
            MultipartFile multiFile = getFile(item);
            if (multiFile == null) {
                continue;
            }
            String fileName = multiFile.getOriginalFilename();
            if (fileName == null || fileName.length() <= 0) {
                continue;
            }
            String filePath = FileUtil.normalize(String.format("%s/%s_%s", localPath, SystemClock.now(), fileName));
            FileUtil.writeFromStream(multiFile.getInputStream(), filePath);
            path[i] = filePath;
        }
        return path;
    }
    // ------------------------文件上传结束
}
