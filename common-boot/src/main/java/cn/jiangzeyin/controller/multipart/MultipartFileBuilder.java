package cn.jiangzeyin.controller.multipart;

import ch.qos.logback.core.util.FileSize;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 文件上传builder
 *
 * @author jiangzeyin
 * 2018/10/23
 */
public class MultipartFileBuilder {

    private MultipartHttpServletRequest multipartHttpServletRequest;
    /**
     * 限制上传文件的大小
     */
    private long maxSize = 0;
    /**
     * 字段名称
     */
    private Set<String> fieldNames = new HashSet<>();
    /**
     * 多文件上传
     */
    private boolean multiple;
    /**
     * 文件名后缀
     */
    private String[] fileExt;
    /**
     * 文件类型
     */
    private String contentTypePrefix;
    /**
     * 文件流类型
     */
    private String[] inputStreamType;
    /**
     * 保存路径
     */
    private String savePath;
    /**
     * 使用原文件名
     */
    private boolean useOriginalFilename;

    /**
     * 文件上传大小限制
     *
     * @param maxSize 字节大小
     * @return this
     */
    public MultipartFileBuilder setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    /**
     * 文件上传大小限制
     *
     * @param maxSize 字符串
     * @return this
     */
    public MultipartFileBuilder setMaxSize(String maxSize) {
        this.maxSize = FileSize.valueOf(maxSize).getSize();
        return this;
    }

    /**
     * 是否使用原文件名保存
     *
     * @param useOriginalFilename true 是
     * @return this
     */
    public MultipartFileBuilder setUseOriginalFilename(boolean useOriginalFilename) {
        this.useOriginalFilename = useOriginalFilename;
        return this;
    }

    /**
     * 需要接受的文件字段
     *
     * @param fieldName 参数名
     * @return this
     */
    public MultipartFileBuilder addFieldName(String fieldName) {
        this.fieldNames.add(fieldName);
        return this;
    }

    /**
     * 清空数据并重新赋值
     *
     * @param fieldName 参数名
     * @return this
     */
    public MultipartFileBuilder resetFieldName(String fieldName) {
        this.fieldNames.clear();
        this.fieldNames.add(fieldName);
        return this;
    }

    /**
     * 是否为多文件上传
     *
     * @param multiple true
     * @return this
     */
    public MultipartFileBuilder setMultiple(boolean multiple) {
        this.multiple = multiple;
        return this;
    }

    /**
     * 限制文件后缀名
     *
     * @param fileExt 后缀
     * @return this
     */
    public MultipartFileBuilder setFileExt(String... fileExt) {
        this.fileExt = fileExt;
        return this;
    }

    /**
     * 限制文件流类型
     *
     * @param inputStreamType type
     * @return this
     * @see FileTypeUtil#getType(java.io.InputStream)
     */
    public MultipartFileBuilder setInputStreamType(String... inputStreamType) {
        this.inputStreamType = inputStreamType;
        return this;
    }

    /**
     * 使用  获取到类型
     *
     * @param contentTypePrefix 前缀
     * @return this
     * @see java.nio.file.Files#probeContentType(java.nio.file.Path)
     */
    public MultipartFileBuilder setContentTypePrefix(String contentTypePrefix) {
        this.contentTypePrefix = contentTypePrefix;
        return this;
    }

    /**
     * 文件保存的路径
     *
     * @param savePath 路径
     * @return this
     */
    public MultipartFileBuilder setSavePath(String savePath) {
        this.savePath = savePath;
        return this;
    }

    private void checkSaveOne() {
        if (this.fieldNames.size() != 1) {
            throw new IllegalArgumentException("fieldNames size:" + this.fieldNames.size() + "  use saves");
        }
        if (this.multiple) {
            throw new IllegalArgumentException("multiple use saves");
        }
    }

    /**
     * 接收单文件上传
     *
     * @return 本地路径
     * @throws IOException IO
     */
    public String save() throws IOException {
        checkSaveOne();
        String[] paths = saves();
        return paths[0];
    }

    /**
     * 保存多个文件
     *
     * @return 本地路径数组
     * @throws IOException IO
     */
    public String[] saves() throws IOException {
        if (fieldNames.isEmpty()) {
            throw new IllegalArgumentException("fieldNames:empty");
        }
        String[] paths = new String[fieldNames.size()];
        int index = 0;
        for (String fieldName : fieldNames) {
            if (this.multiple) {
                List<MultipartFile> multipartFiles = multipartHttpServletRequest.getFiles(fieldName);
                for (MultipartFile multipartFile : multipartFiles) {
                    paths[index++] = saveAndName(multipartFile)[0];
                }
            } else {
                MultipartFile multipartFile = multipartHttpServletRequest.getFile(fieldName);
                paths[index++] = saveAndName(multipartFile)[0];
            }
        }
        return paths;
    }

    /**
     * 上传文件，并且返回原文件名
     *
     * @return 数组
     * @throws IOException IO
     */
    public String[] saveAndName() throws IOException {
        checkSaveOne();
        List<String[]> list = saveAndNames();
        return list.get(0);
    }

    /**
     * 上传文件，并且返回原文件名
     *
     * @return 集合
     * @throws IOException IO
     */
    public List<String[]> saveAndNames() throws IOException {
        if (fieldNames.isEmpty()) {
            throw new IllegalArgumentException("fieldNames:empty");
        }
        List<String[]> list = new ArrayList<>();
        for (String fieldName : fieldNames) {
            if (this.multiple) {
                List<MultipartFile> multipartFiles = multipartHttpServletRequest.getFiles(fieldName);
                for (MultipartFile multipartFile : multipartFiles) {
                    String[] info = saveAndName(multipartFile);
                    list.add(info);
                }
            } else {
                MultipartFile multipartFile = multipartHttpServletRequest.getFile(fieldName);
                String[] info = saveAndName(multipartFile);
                list.add(info);
            }
        }
        return list;
    }

    /**
     * 保存文件并验证类型
     *
     * @param multiFile file
     * @return 本地路径和原文件名
     * @throws IOException IO
     */
    private String[] saveAndName(MultipartFile multiFile) throws IOException {
        String fileName = multiFile.getOriginalFilename();
        if (StrUtil.isEmpty(fileName)) {
            throw new IllegalArgumentException("fileName:不能获取到文件名");
        }
        long fileSize = multiFile.getSize();
        if (fileSize <= 0) {
            throw new IllegalArgumentException("fileSize:文件内容为空");
        }
        // 文件名后缀
        if (this.fileExt != null) {
            String checkName = fileName.toLowerCase();
            boolean find = false;
            for (String ext : this.fileExt) {
                find = checkName.endsWith("." + ext.toLowerCase());
                if (find) {
                    break;
                }
            }
            if (!find) {
                throw new IllegalArgumentException("fileExt:类型错误:" + checkName);
            }
        }
        // 文件大小
        if (maxSize > 0 && fileSize > maxSize) {
            throw new IllegalArgumentException("maxSize:too big:" + fileSize + ">" + maxSize);
        }
        // 文件流类型
        if (this.inputStreamType != null) {
            InputStream inputStream = multiFile.getInputStream();
            String fileType = FileTypeUtil.getType(inputStream);
            for (String type : this.inputStreamType) {
                if (type.equalsIgnoreCase(fileType)) {
                    continue;
                }
                throw new IllegalArgumentException("inputStreamType:类型错误:" + fileType + "  " + type);
            }
        }
        // 保存路径
        String localPath;
        if (this.savePath != null) {
            localPath = this.savePath;
        } else {
            localPath = MultipartFileConfig.getFileTempPath();
        }
        // 保存的文件名
        String filePath;
        if (useOriginalFilename) {
            filePath = FileUtil.normalize(String.format("%s/%s", localPath, fileName));
        } else {
            // 防止中文乱码
            String saveFileName = UnicodeUtil.toUnicode(fileName);
            // 生成唯一id
            filePath = FileUtil.normalize(String.format("%s/%s_%s", localPath, IdUtil.objectId(), saveFileName));
        }
        FileUtil.writeFromStream(multiFile.getInputStream(), filePath);
        // 文件contentType
        if (this.contentTypePrefix != null) {
            Path source = Paths.get(filePath);
            String contentType = Files.probeContentType(source);
            if (contentType == null) {
                // 自动清理文件
                FileUtil.del(filePath);
                throw new IllegalArgumentException("contentTypePrefix:获取文件类型失败");
            }
            if (!contentType.startsWith(contentTypePrefix)) {
                // 自动清理文件
                FileUtil.del(filePath);
                throw new IllegalArgumentException("contentTypePrefix:文件类型不正确:" + contentType);
            }
        }
        return new String[]{filePath, fileName};
    }

    public MultipartFileBuilder(MultipartHttpServletRequest multipartHttpServletRequest) {
        Objects.requireNonNull(multipartHttpServletRequest);
        this.multipartHttpServletRequest = multipartHttpServletRequest;
    }
}
