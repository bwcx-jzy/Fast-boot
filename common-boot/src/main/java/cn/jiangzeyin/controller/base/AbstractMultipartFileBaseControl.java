package cn.jiangzeyin.controller.base;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.common.request.ParameterXssWrapper;
import cn.jiangzeyin.controller.multipart.MultipartFileConfig;
import cn.jiangzeyin.util.FileUtil;
import cn.jiangzeyin.util.ReflectUtil;
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
        return MAP_THREAD_LOCAL_PARAMETER.get();
    }

    protected MultipartHttpServletRequest getMultiRequest() {
        MultipartHttpServletRequest multipartHttpServletRequest = THREAD_LOCAL_MULTIPART_HTTP_SERVLET_REQUEST.get();
        Assert.notNull(multipartHttpServletRequest);
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
        }
    }

    @Override
    public HttpServletRequest getRequest() {
        if (getMultiRequest() == null)
            return super.getRequest();
        return getMultiRequest();
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
     * 获取上传文件对象信息
     * 不能用于接收图片
     *
     * @param cls  cls
     * @param path path
     * @param name names
     * @param <T>  t
     * @return t
     * @throws IllegalAccessException y
     * @throws InstantiationException y
     * @throws IOException            y
     */
    protected <T> T getUpload(Class<T> cls, String path, String... name) throws IllegalAccessException, InstantiationException, IOException {
        Map<String, String[]> parameter = getParameter();
        Assert.notNull(parameter);
        if (name == null || name.length <= 0)
            return null;
        path = StringUtil.convertNULL(path);
        Object object = cls.newInstance();
        String localPath = MultipartFileConfig.getFileTempPath();
        for (String aName : name) {
            MultipartFile multiFile = getFile(aName);
            if (multiFile == null)
                continue;
            String fileName = multiFile.getOriginalFilename();
            if (fileName == null || fileName.length() <= 0)
                continue;
            String filePath = StringUtil.clearPath(String.format("%s/%s/%s_%s", localPath, path, System.currentTimeMillis(), fileName));
            //File file = ;
            //FileUtil.mkdirs(file);
            boolean flag = FileUtil.writeInputStream(multiFile.getInputStream(), new File(filePath));
            if (!flag)
                throw new RuntimeException(filePath + " write fail");
            ReflectUtil.setFieldValue(object, aName, filePath);
        }
        doParameterMap(parameter, object);
        return (T) object;
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
