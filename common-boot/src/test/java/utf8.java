import cn.hutool.core.convert.Convert;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by jiangzeyin on 2018/8/21.
 */
public class utf8 {
    public static void main(String[] args) throws UnsupportedEncodingException {

        System.out.println(Convert.toInt("sss", 1));

        System.out.println("系统默认编码：" + System.getProperty("file.encoding")); //查询结果GBK
        //系统默认字符编码
        System.out.println("系统默认字符编码：" + Charset.defaultCharset()); //查询结果GBK
        //操作系统用户使用的语言
        System.out.println("系统默认语言：" + System.getProperty("user.language")); //查询结果zh

        String test = "测试";
        System.out.println(test);
        System.out.println(isUTF8(test));

        String test2 = new String(test.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
        System.out.println(test2);
        System.out.println(isUTF8(test2));

        System.out.println(getEncoding(test2));
    }

    public static boolean isUTF8(String key) {
        System.out.println(key + "  " + new String(key.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        return key.equals(new String(key.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
    }

    public static String getEncoding(String str) {
        String encode[] = new String[]{
                "UTF-8",
                "ISO-8859-1",
                "GB2312",
                "GBK",
                "GB18030",
                "Big5",
                "Unicode",
                "ASCII"
        };
        for (String anEncode : encode) {
            try {
                if (str.equals(new String(str.getBytes(anEncode), anEncode))) {
                    return anEncode;
                }
            } catch (Exception ignored) {
            }
        }

        return "";
    }
}
