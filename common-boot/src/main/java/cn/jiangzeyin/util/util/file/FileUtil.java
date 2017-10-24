package cn.jiangzeyin.util.util.file;

import org.springframework.util.Assert;

import java.io.*;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Properties;

/**
 * 文件操作工具类
 *
 * @author jiangzeyin
 */
public final class FileUtil {

    /**
     * @param path path
     * @return p
     * @throws IOException io
     * @author jiangzeyin
     */
    public static Properties getProperties(String path) throws IOException {
        Properties prop = new Properties();
        InputStream in = new FileInputStream(path);
        prop.load(in);
        return prop;
    }

    /**
     * 格式化单位
     *
     * @param size size
     * @return str
     */
    public static String getFormatSize(long size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    /**
     * 判断文件是否存在
     *
     * @param path path
     * @return boolean
     */
    public static boolean exists(String path) {
        File f = new File(path);
        return f.exists();
    }

    /**
     * 创建文件路径
     *
     * @param path path
     * @return boolean
     * @author jiangzeyin
     */
    public static boolean mkdirs(String path) {
        return mkdirs(new File(path), path.endsWith("/"));
    }

    /**
     * @param file   file
     * @param isPath isPath
     * @return boolean
     * @author jiangzeyin
     */
    public static boolean mkdirs(File file, boolean isPath) {
        if (file == null)
            return false;
        if (isPath)
            return file.mkdirs();
        else
            return file.getParentFile().mkdirs();
    }

    public static boolean mkdirs(File file) {
        return mkdirs(file, false);
    }

    /**
     * 读取文件全部内容
     *
     * @param file     file
     * @param encoding encoding
     * @return str
     * @throws IOException io
     */
    public static String readToString(File file, String encoding) throws IOException {
        BufferedReader br;
        StringBuffer stringBuffer = null;
        br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        String line = null;
        stringBuffer = new StringBuffer();
        while ((line = br.readLine()) != null) {
            stringBuffer.append(line);
        }
        br.close();
        return stringBuffer.toString();// 返回文件内容,默认编码
    }

    public static String readToString(File file) throws IOException {
        return readToString(file, "UTF-8");
    }

    public static String getFileEncode(String path) throws IOException {
        String charset = "asci";
        byte[] first3Bytes = new byte[3];

        boolean checked = false;
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
        bis.mark(0);
        int read = bis.read(first3Bytes, 0, 3);
        if (read == -1)
            return charset;
        if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
            charset = "Unicode";// UTF-16LE
            checked = true;
        } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
            charset = "Unicode";// UTF-16BE
            checked = true;
        } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
            charset = "UTF8";
            checked = true;
        }
        bis.reset();
        if (!checked) {
            while ((read = bis.read()) != -1) {
                if (read >= 0xF0)
                    break;
                if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                    break;
                if (0xC0 <= read && read <= 0xDF) {
                    read = bis.read();
                    if (0x80 <= read && read <= 0xBF)
                        // 双字节 (0xC0 - 0xDF) (0x80 - 0xBF),也可能在GB编码内
                        continue;
                    else
                        break;
                } else if (0xE0 <= read && read <= 0xEF) { // 也有可能出错，但是几率较小
                    read = bis.read();
                    if (0x80 <= read && read <= 0xBF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            charset = "UTF-8";
                            break;
                        } else
                            break;
                    } else
                        break;
                }
            }
            // TextLogger.getLogger().info(loc + " " +
            // Integer.toHexString(read));
        }
        bis.close();
        return charset;
    }

    public static String readFile(String path) throws IOException {
        // 判断文件是否存在
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        String data = null;
        // 获取文件编码格式
        String code = FileUtil.getFileEncode(path);
        InputStreamReader isr = null;
        // 根据编码格式解析文件
        if ("asci".equals(code)) {
            // 这里采用GBK编码，而不用环境编码格式，因为环境默认编码不等于操作系统编码
            // code = System.getProperty("file.encoding");
            code = "GBK";
        }
        isr = new InputStreamReader(new FileInputStream(file), code);
        // 读取文件内容
        int length = -1;
        char[] buffer = new char[1024];
        StringBuffer sb = new StringBuffer();
        while ((length = isr.read(buffer, 0, 1024)) != -1) {
            sb.append(buffer, 0, length);
        }
        data = new String(sb);
        isr.close();
        return data;
    }

    /**
     * 读取文件
     *
     * @param fileName fileName
     * @param encoding encoding
     * @return strng
     * @throws Exception e
     * @author jiangzeyin
     */
    public static String readToString(String fileName, String encoding) throws Exception {
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();
        return new String(filecontent, encoding);
    }

    /**
     * 读取文件全部内容
     * <p>
     * 默认为UTF-8
     *
     * @param fileName fileName
     * @return str
     * @throws Exception e
     * @author jiangzeyin
     */
    public static String readToString(String fileName) throws Exception {
        return readToString(fileName, "UTF-8");
    }

    /**
     * 写文件
     *
     * @param fileName fileName
     * @param content  content
     * @throws IOException io
     * @author jiangzeyin
     */
    public static void writeFile(String fileName, String content) throws IOException {
        appendFileContext(fileName, content, false);
    }

    /**
     * 添加文件内容
     *
     * @param fileName file
     * @param content  c
     * @throws IOException io
     * @author jiangzeyin
     */
    public static void appendFileContext(String fileName, String content) throws IOException {
        appendFileContext(fileName, content, true);
    }

    /**
     * 追加文件内容
     *
     * @param fileName fileName
     * @param content  c
     * @param append   是否是追加
     * @throws IOException io
     * @author jiangzeyin
     */
    public static void appendFileContext(String fileName, String content, boolean append) throws IOException {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
        if (content != null)
            out.write(content);
        out.close();
    }

    public static boolean writeInputStream(InputStream inputStream, String path) throws IOException {
        return writeInputStream(inputStream, new File(path));
    }


    /**
     * @param inputStream inp
     * @param file        file
     * @return boolean
     * @throws IOException io
     */
    public static boolean writeInputStream(InputStream inputStream, File file) throws IOException {
        Assert.notNull(inputStream);
        Assert.notNull(file);
        mkdirs(file);
        DataOutputStream outputStream = null;
        try {
            outputStream = new DataOutputStream(new FileOutputStream(file));
            int len = inputStream.available();
            //判断长度是否大于1M
            if (len <= 1024 * 1024) {
                byte[] bytes = new byte[len];
                inputStream.read(bytes);
                outputStream.write(bytes);
            } else {
                int byteCount = 0;
                //1M逐个读取
                byte[] bytes = new byte[1024 * 1024];
                while ((byteCount = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, byteCount);
                }
            }
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        }
        return true;
    }

    /**
     * 判断流的字符串格式
     *
     * @param ins ins
     * @return str
     * @throws IOException io
     */
    public static String getCharset(InputStream ins) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(ins);
        int p = (bin.read() << 8) + bin.read();
        String code = null;
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        ins.close();
        bin.close();
        return code;
    }

    /**
     * 获取文件 编码
     *
     * @param fileName fileName
     * @return str
     * @throws IOException io
     * @author jiangzeyin
     */
    public static String getFileCharset(String fileName) throws IOException {
        FileInputStream ins = new FileInputStream(fileName);
        return getCharset(ins);
    }


    public static String getFilePath(String path) {
        File file = new File(path);
        return getFilePath(file);
    }

    public static String getFilePath(File file) {
        return clearPath(file.getParent());
    }

    /**
     * 获取文件后缀
     *
     * @param filename file
     * @return str
     * @author jiangzeyin
     */
    public static String getFileExt(String filename) {
        String ext = "";
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        ext = filename.substring(index + 1);
        return ext;
    }

    public static String clearPath(String input) {
        input = input.replace('\\', '/');
        return clearPath_(input);
    }

    /**
     * 获取文件名
     *
     * @param filename file
     * @return s
     * @author jiangzeyin
     */
    public static String getFileName(String filename) {
        String ext = "";
        int index = filename.lastIndexOf("/");
        if (index == -1) {
            return filename;
        }
        ext = filename.substring(index + 1);
        return ext;
    }

    private static String clearPath_(String input) {
        int from = 0;
        int j = input.indexOf("://");
        if (j != -1) {
            from = j + 3;
        }
        int i = input.indexOf("//", from);
        if (i == -1) {
            return input;
        }

        String input_ = input.substring(0, i) + "/" + input.substring(i + 2);
        return clearPath_(input_);
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful. If a
     * deletion fails, the method stops attempting to delete and returns
     * "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < (children != null ? children.length : 0); i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 获取指定目录下的所有文件
     *
     * @param path path
     * @return LinkedList
     * @author XiangZhongBao
     */
    public static LinkedList<File> getFolderFiles(String path) {
        File file = new File(path);
        if (!file.exists())
            return null;
        if (!file.isDirectory())
            return null;
        File[] files = file.listFiles();
        if (files == null)
            return null;
        LinkedList<File> linkedList = new LinkedList<>();
        for (File item : files) {
            if (item.isDirectory()) {
                LinkedList<File> tempFile = getFolderFiles(item.getPath());
                if (tempFile != null)
                    linkedList.addAll(tempFile);
            } else {
                linkedList.add(item);
            }
        }
        return linkedList;
    }

    /**
     * @param dir dir
     * @return boolean
     * @author jiangzeyin
     */
    public static boolean deleteDir(String dir) {
        return deleteDir(new File(dir));
    }
}

