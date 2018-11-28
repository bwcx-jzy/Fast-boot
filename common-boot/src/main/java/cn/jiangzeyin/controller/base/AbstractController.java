package cn.jiangzeyin.controller.base;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpUtil;
import cn.jiangzeyin.common.interceptor.BaseCallbackController;
import cn.jiangzeyin.controller.multipart.MultipartFileBuilder;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * base
 * 公共的获取参数
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/12.
 */
public abstract class AbstractController extends BaseCallbackController {

    /**
     * 拦截器注入
     */
    @Override
    public void resetInfo() {

    }

    /**
     * 获取请求的ip 地址
     *
     * @return ip
     */
    protected String getIp() {
        return ServletUtil.getClientIP(getRequest());
    }

    /**
     * 获取指定header
     *
     * @param name name
     * @return value
     */
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
        return HttpUtil.decodeParamMap(referer, CharsetUtil.UTF_8);
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
        return getHeaderMapValues(getRequest());
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
    /**
     * cache
     */
    private static final ThreadLocal<MultipartHttpServletRequest> THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST = new ThreadLocal<>();

    /**
     * 释放资源
     */
    public static void clearResources() {
        THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.remove();
    }

    /**
     * 获取文件上传请求对象
     *
     * @return multipart
     */
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

    /**
     * 判断是否存在文件
     *
     * @return true 存在文件
     */
    protected boolean hasFile() {
        Map<String, MultipartFile> fileMap = getMultiRequest().getFileMap();
        return fileMap != null && fileMap.size() > 0;
    }

    /**
     * 创建多文件上传对象
     *
     * @return MultipartFileBuilder
     */
    protected MultipartFileBuilder createMultipart() {
        return new MultipartFileBuilder(getMultiRequest());
    }
    // ------------------------文件上传结束
}
