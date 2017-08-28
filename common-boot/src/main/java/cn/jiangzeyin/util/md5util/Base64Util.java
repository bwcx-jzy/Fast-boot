package cn.jiangzeyin.util.md5util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * @author jiangzeyin
 */
public final class Base64Util {

    private static final BASE64Encoder base64en = new BASE64Encoder();

    private static final BASE64Decoder base64Decode = new BASE64Decoder();

    /**
     * @param input inp
     * @return str
     * @throws Exception e
     */
    public static String encodeBase64Stream(InputStream input) throws Exception {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc = 0;
        while ((rc = input.read(buff, 0, 1024)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return base64en.encode(swapStream.toByteArray());
    }

    public static byte[] decoderBase64Byte(String base64Code) throws Exception {
        return new BASE64Decoder().decodeBuffer(base64Code);
    }

    public static boolean GenerateImage(String imgStr, String path) throws IOException { // 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) // 图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        // Base64解码
        byte[] b = decoder.decodeBuffer(imgStr);
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {// 调整异常数据
                b[i] += 256;
            }
        }
        File f = new File(path);
        f.getParentFile().mkdirs();
        // 生成jpeg图片
        // String imgFilePath = "C:/test22.png";// 新生成的图片
        OutputStream out = new FileOutputStream(path);
        out.write(b);
        out.flush();
        out.close();
        return true;
    }

    /**
     * 对字符串进行Base64编码
     *
     * @param src 要进行编码的字符
     * @return String 进行编码后的字符串
     */
    public static String getEncode(byte[] src) {
        String requestValue = base64en.encode(src);
        return requestValue;
    }

    public static byte[] decodeBuffer(String value) throws IOException {
        return base64Decode.decodeBuffer(value);
    }
}
