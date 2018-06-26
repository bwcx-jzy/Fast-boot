package cn.jiangzeyin.controller.base;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.SystemClock;
import cn.jiangzeyin.common.request.ParameterXssWrapper;
import cn.jiangzeyin.controller.multipart.MultipartFileConfig;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 上传文件control
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/13.
 */
public abstract class AbstractMultipartFileBaseControl extends AbstractBaseControl {

    private static final ThreadLocal<MultipartHttpServletRequest> THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST = new ThreadLocal<>();

    protected MultipartHttpServletRequest getMultiRequest() {
        MultipartHttpServletRequest multipartHttpServletRequest = THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.get();
        if (multipartHttpServletRequest != null)
            return multipartHttpServletRequest;
        HttpServletRequest request = super.getRequest();
        if (ServletFileUpload.isMultipartContent(request)) {
            if (request instanceof MultipartHttpServletRequest) {
                return (MultipartHttpServletRequest) request;
            }
            multipartHttpServletRequest = new StandardMultipartHttpServletRequest(request);
            THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.set(multipartHttpServletRequest);
            return multipartHttpServletRequest;
        } else {
            throw new IllegalArgumentException("not is Multipart");
        }
    }

    /**
     * 处理上传文件 对象
     *
     * @param request  req
     * @param session  ses
     * @param response res
     */
    public void setReqAndRes(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.set(null);
    }

    @Override
    protected HttpServletRequest getRequest() {
        HttpServletRequest request;
        try {
            request = getMultiRequest();
        } catch (Exception e) {
            return super.getRequest();
        }
        return request;
    }

    protected MultipartFile getFile(String name) {
        return getMultiRequest().getFile(name);
    }

    protected List<MultipartFile> getFiles(String name) {
        return getMultiRequest().getFiles(name);
    }

    @Override
    protected <T> T getObject(Class<T> tClass) throws IllegalAccessException, InstantiationException {
        Map<String, String[]> parameter = getParameter();
        if (parameter == null)
            return super.getObject(tClass);
        Object object = tClass.newInstance();
        doParameterMap(parameter, object);
        return (T) object;
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
        for (int i = 0, len = path.length; i < len; i++) {
            String item = name[i];
            String localPath = MultipartFileConfig.getFileTempPath();
            MultipartFile multiFile = getFile(item);
            if (multiFile == null)
                continue;
            String fileName = multiFile.getOriginalFilename();
            if (fileName == null || fileName.length() <= 0)
                continue;
            String filePath = StringUtil.clearPath(String.format("%s/%s_%s", localPath, SystemClock.now(), fileName));
            multiFile.transferTo(new File(filePath));
            //FileUtil.writeInputStream(multiFile.getInputStream(), new File(filePath));
            path[i] = filePath;
        }
        return path;
    }

    private Map<String, String[]> getParameter() {
        Map<String, String[]> map = getRequest().getParameterMap();
        boolean doXss = Boolean.valueOf(String.valueOf(getAttribute("ParameterXssWrapper.doXss")));
        if (!doXss) {
            map = ParameterXssWrapper.doXss(map, false);
            setAttribute("ParameterXssWrapper.doXss", true);
        }
        return map;
    }

    @Override
    protected String[] getParameters(String name) {
        Map<String, String[]> parameter = getParameter();
        if (parameter == null)
            return super.getParameters(name);
        return parameter.get(name);
    }

    @Override
    protected String getParameter(String name) {
        String[] values = getParameters(name);
        return values == null ? null : values[0];
    }

    @Override
    protected String getParameter(String name, String def) {
        String value = getParameter(name);
        return value == null ? def : value;
    }

    @Override
    protected int getParameterInt(String name) {
        return getParameterInt(name, 0);
    }

    @Override
    protected int getParameterInt(String name, int def) {
        String value = getParameter(name);
        return StringUtil.parseInt(value, def);
    }

    @Override
    protected long getParameterLong(String name) {
        return getParameterLong(name, 0L);
    }

    @Override
    protected long getParameterLong(String name, long def) {
        String value = getParameter(name);
        if (value == null)
            return def;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
        }
        return def;
    }
}
