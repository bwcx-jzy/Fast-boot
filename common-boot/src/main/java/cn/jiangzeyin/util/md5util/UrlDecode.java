package cn.jiangzeyin.util.md5util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by jiangzeyin on 2017/3/7.
 */
public class UrlDecode {
    /**
     * 对字符串进行URLDecoder.decode(strEncoding)解码
     *
     * @param src      要进行解码的字符串
     * @param encoding e
     * @return String 进行解码后的字符串
     * @throws UnsupportedEncodingException e
     */
    public static String getURLDecode(String src, String encoding) throws UnsupportedEncodingException {
        return URLDecoder.decode(src.intern(), encoding.intern());
    }

    public static String getURLDecode(String src) throws UnsupportedEncodingException {
        return URLDecoder.decode(src.intern(), "UTF-8");
    }

    /**
     * 对字符串进行URLDecoder.encode(strEncoding)编码
     *
     * @param src      要进行编码的字符串
     * @param encoding e
     * @return String 进行编码后的字符串
     * @throws UnsupportedEncodingException u
     */
    public static String getURLEncode(String src, String encoding) throws UnsupportedEncodingException {
        return URLEncoder.encode(src.intern(), encoding.intern());
    }

    public static String getURLEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src.intern(), "UTF-8");
    }

}
