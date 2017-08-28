package cn.jiangzeyin.util.util.file;

import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jiangzeyin on 2017/3/15.
 */
public abstract class FileStreamUtil {

    /**
     * 复制Input流
     *
     * @param input inp
     * @return inp
     * @throws Exception e
     * @author jiangzeyin
     */
    public static InputStream copyInputStream(InputStream input) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byteArrayOutputStream.flush();
        //input = new ByteArrayInputStream(baos.toByteArray());
        InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return stream;
    }

    /**
     * @param is is
     * @return fileType
     * @throws IOException io
     * @author jiangzeyin
     */
    public static FileType getFileType(InputStream is) throws IOException {
        Assert.notNull(is);
        byte[] src = new byte[28];
        is.read(src, 0, 28);
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        //System.out.println(stringBuilder);
        FileType[] fileTypes = FileType.values();
        for (FileType fileType : fileTypes) {
            if (stringBuilder.toString().startsWith(fileType.getValue())) {
                return fileType;
            }
        }
        return null;
    }
}
