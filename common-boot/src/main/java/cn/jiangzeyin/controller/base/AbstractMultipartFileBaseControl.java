package cn.jiangzeyin.controller.base;

import cn.jiangzeyin.common.request.ParameterXssWrapper;
import cn.jiangzeyin.util.ref.ReflectUtil;
import cn.jiangzeyin.util.util.StringUtil;
import cn.jiangzeyin.util.util.file.FileStreamUtil;
import cn.jiangzeyin.util.util.file.FileType;
import cn.jiangzeyin.util.util.file.FileUtil;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 上传文件control
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/13.
 */
public abstract class AbstractMultipartFileBaseControl extends AbstractBaseControl {
    private Map<String, String[]> parameter;
    private MultipartHttpServletRequest multiRequest;


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
            multiRequest = (MultipartHttpServletRequest) request;
            parameter = ParameterXssWrapper.doXss(multiRequest.getParameterMap(), false);
        }
    }

    @Override
    public HttpServletRequest getRequest() {
        if (multiRequest == null)
            return super.getRequest();
        return multiRequest;
    }

    protected MultipartFile getFile(String name) {
        Assert.notNull(multiRequest);
        return multiRequest.getFile(name);
    }

    protected List<MultipartFile> getFiles(String name) {
        Assert.notNull(multiRequest);
        return multiRequest.getFiles(name);
    }


    /**
     * 判断文件类型
     *
     * @param fileTypes   types
     * @param name        name
     * @param inputStream inp
     * @return boolean
     * @throws IOException io
     */
    protected static boolean checkExt(FileType[] fileTypes, String name, InputStream inputStream) throws IOException {
        Assert.notNull(inputStream);
        if (fileTypes == null)
            return true;
        FileType fileType = FileStreamUtil.getFileType(inputStream);
        String fileExt = FileUtil.getFileExt(name);
        for (FileType item : fileTypes) {
            if (fileType == item) {
                if (item.getExt().equalsIgnoreCase(fileExt)) {
                    return true;
                }
            }
        }
        return false;
//        boolean find = false;
//        for (String item : ext) {
//            if ((find = item.equalsIgnoreCase(fileExt)))
//                return true;
//        }
//        return find;
    }

    @Override
    public <T> T getObject(Class<T> tClass) throws IllegalAccessException, InstantiationException {
        if (parameter == null)
            return super.getObject(tClass);
        Object object = tClass.newInstance();
        //doObject(parameter.entrySet().iterator(), object);
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
        Assert.notNull(parameter);
        Assert.notNull(multiRequest);
        if (name == null || name.length <= 0)
            return null;
        path = StringUtil.convertNULL(path);
        Object object = cls.newInstance();
        String localPath = null;//SiteCache.currentSite.getLocalPath();
        //String fileTempPath = FileUtil.clearPath(ServiceInfoUtil.getTomcatTempPath() + "/" + localPath + "/" + path);
        //FileUtil.mkdirs(fileTempPath);
        //String[] paths = new String[name.length];
        for (String aName : name) {
            MultipartFile multiFile = getFile(aName);
            if (multiFile == null)
                continue;
            //MultipartFile multiFile = multiEntry.getValue();
            String fileName = multiFile.getOriginalFilename();
            if (StringUtil.isEmpty(fileName))
                continue;
            String filePath = FileUtil.clearPath(String.format("%s/%s/%s_%s", localPath, path, System.currentTimeMillis(), fileName));
            //File file = ;
            //FileUtil.mkdirs(file);
            FileUtil.writeInputStream(multiFile.getInputStream(), new File(filePath));
            ReflectUtil.setFieldValue(object, aName, filePath);
        }
        doParameterMap(parameter, object);
        return (T) object;
    }

    @Override
    public String[] getParameters(String name) {
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
