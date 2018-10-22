import cn.hutool.core.io.FileUtil;
import org.springframework.util.Assert;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by jiangzeyin on 2018/5/6.
 */
public class ZIP {
    public static void main(String[] args) throws Exception {
        String path = "D:\\SystemDocument\\Desktop\\newEdit\\js\\js_ace.zip";
//        path = "D:\\SystemDocument\\Desktop\\Samples.zip";
        ZipFile zipFile = new ZipFile(path);
        Enumeration<ZipEntry> zipEntryEnumeration = (Enumeration<ZipEntry>) zipFile.entries();
        while (zipEntryEnumeration.hasMoreElements()) {
            ZipEntry zipEntry = zipEntryEnumeration.nextElement();
            if (zipEntry.isDirectory()) {
                continue;
            }
            long size = zipEntry.getSize();
            //long size = ze.getSize();
            String keyName = zipEntry.getName();
            if (!"js_ace/require.js".equals(keyName))
                continue;
            // String filePath_item = StringUtil.clearPath(String.format("%s/%s_%s_childs/%s", localPath, randName, fileName, keyName));
            InputStream inputStream = zipFile.getInputStream(zipEntry);
            System.out.println(inputStream.getClass());
            FileUtil.writeFromStream(inputStream, new File("D:\\SystemDocument\\Desktop\\newEdit\\js\\t1.js"));
        }
        zipFile.close();
    }

    public static boolean writeInputStream(InputStream inputStream, File file) throws IOException {
        Assert.notNull(inputStream, "inputStream is null");
        Assert.notNull(file, "file is null");
        File parent = file.getParentFile();
        if (!parent.exists())
            if (!parent.mkdirs())
                throw new IllegalArgumentException(file.getPath() + " create fail");
        DataOutputStream outputStream = null;
        try {
            outputStream = new DataOutputStream(new FileOutputStream(file));
            int len = inputStream.available();

            //判断长度是否大于1M
            if (len <= 1024 * 1024) {
                System.out.println(len);
                byte[] bytes = new byte[len];
                int rLen = inputStream.read(bytes, 0, len);
                System.out.println(inputStream.available() + "  " + len + "  " + rLen);
                if (rLen != -1)
                    outputStream.write(bytes, 0, len);
            } else {
                System.out.println("1M");
                int byteCount;
                //1M逐个读取
                byte[] bytes = new byte[1024 * 1024];
                while ((byteCount = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, byteCount);
                }
            }
        } finally {
            inputStream.close();
            if (outputStream != null)
                outputStream.close();
        }
        return true;
    }
}
