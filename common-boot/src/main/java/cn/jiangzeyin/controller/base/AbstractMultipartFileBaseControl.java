package cn.jiangzeyin.controller.base;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.SystemClock;
import cn.jiangzeyin.common.request.ParameterXssWrapper;
import cn.jiangzeyin.controller.multipart.MultipartFileConfig;
import cn.jiangzeyin.util.FileUtil;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
    private static final ThreadLocal<Map<String, String[]>> MAP_THREAD_LOCAL_PARAMETER = new ThreadLocal<>();
    private static final ThreadLocal<MultipartHttpServletRequest> THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST = new ThreadLocal<>();

    protected Map<String, String[]> getParameter() {
        Map<String, String[]> map = MAP_THREAD_LOCAL_PARAMETER.get();
        if (map == null) {
            try {
                map = ParameterXssWrapper.doXss(getMultiRequest().getParameterMap(), false);
                MAP_THREAD_LOCAL_PARAMETER.set(map);
            } catch (Exception ignored) {
            }
        }
        return map;
    }

    protected MultipartHttpServletRequest getMultiRequest() {
        MultipartHttpServletRequest multipartHttpServletRequest = THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.get();
        if (multipartHttpServletRequest == null) {
            HttpServletRequest request = super.getRequest();
            if (request instanceof MultipartHttpServletRequest) {
                multipartHttpServletRequest = (MultipartHttpServletRequest) request;
                THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.set(multipartHttpServletRequest);
            }
        }
        Assert.notNull(multipartHttpServletRequest, "not is Multipart");
        return multipartHttpServletRequest;
    }

    /**
     * 处理上传文件 对象
     *
     * @param request  req
     * @param session  ses
     * @param response res
     */
    @Override
    public void setReqAndRes(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        super.setReqAndRes(request, session, response);
        if (ServletFileUpload.isMultipartContent(request)) {
            THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.set((MultipartHttpServletRequest) request);
            MAP_THREAD_LOCAL_PARAMETER.set(ParameterXssWrapper.doXss(getMultiRequest().getParameterMap(), false));
        } else {
            THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.set(null);
            MAP_THREAD_LOCAL_PARAMETER.set(null);
        }
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
            FileUtil.writeInputStream(multiFile.getInputStream(), new File(filePath));
            path[i] = filePath;
        }
        return path;
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
}
