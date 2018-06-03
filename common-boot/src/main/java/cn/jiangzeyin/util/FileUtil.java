package cn.jiangzeyin.util;

import java.io.*;
import java.util.Objects;

/**
 * 文件操作工具类
 *
 * @author jiangzeyin
 */
public final class FileUtil {

    public static int EACH_LEN = 1024 * 1024;

    /**
     * 复制流
     *
     * @param input input
     * @return 新流
     * @throws IOException io
     */
    public static InputStream copyInputStream(InputStream input) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int len = input.available();
        //判断长度是否大于1M  如果大于1M逐个读取
        int byteLen = len > EACH_LEN ? EACH_LEN : len;
        byte[] bytes = new byte[byteLen];
        //  byte[] buffer = new byte[1024];
        while ((len = input.read(bytes)) > -1) {
            byteArrayOutputStream.write(bytes, 0, len);
        }
        byteArrayOutputStream.flush();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }


    /**
     * input 流字符串
     *
     * @param inputStream inp
     * @return str
     * @throws IOException io
     */
    public static String inputStreamToString(InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);
        StringBuilder sb = new StringBuilder();
        String line;
        InputStreamReader inputStreamReader = null;
        BufferedReader br = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
            br = new BufferedReader(inputStreamReader);
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (br != null)
                br.close();
            if (inputStreamReader != null)
                inputStreamReader.close();
            inputStream.close();
        }
        return sb.toString();
    }

    /**
     * 将流写入文件中
     *
     * @param inputStream inp
     * @param file        file
     * @throws IOException io
     */
    public static void writeInputStream(InputStream inputStream, File file) throws IOException {
        Objects.requireNonNull(inputStream, "inputStream is null");
        Objects.requireNonNull(file, "file is null");
        File parent = file.getParentFile();
        if (!parent.exists())
            if (!parent.mkdirs())
                throw new IllegalArgumentException(file.getPath() + " create fail");
        DataOutputStream outputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            outputStream = new DataOutputStream(fileOutputStream);
            int len = inputStream.available();
            //判断长度是否大于1M  如果大于1M逐个读取
            int byteLen = len > EACH_LEN ? EACH_LEN : len;
            byte[] bytes = new byte[byteLen];
            int byteCount;
            while ((byteCount = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, byteCount);
            }
        } finally {
            inputStream.close();
            if (outputStream != null)
                outputStream.close();
            if (fileOutputStream != null)
                fileOutputStream.close();
        }
    }

//    public static boolean deleteDirectory(File dir) {
//        if (!dir.exists())
//            return true;
//        Queue<File> queue = new LinkedBlockingQueue<>();
//        queue.add(dir);
//        System.out.println(dir.getPath());
//        while (!queue.isEmpty()) {
//            File file = queue.poll();
//            File[] files = file.listFiles();
//            if (files != null && files.length > 0) {
//                Collections.addAll(queue, files);
////                    queue.add()
//                //queue.element()
////                    queue.offer(Arrays.asList(files));
//                //queue.add(file);
//                //queue.add(file);
//                //stack.push(file);
//                FileUtils.deleteDirectory();
//                System.out.println(file.getPath());
//            } else if (!file.delete()) {
//                System.err.println(file.getPath());
//                return false;
//            }
//            System.out.println("ml" + file.getPath() + "  " + queue.size());
//        }
//        return true;
//    }

}

