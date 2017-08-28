package cn.jiangzeyin.util.md5util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2016/12/12.
 */
public class MD5Utils {
    /**
     * 进行MD5加密
     * <p>
     * 原始的SPKEY
     *
     * @param strSrc   str
     * @param encoding e
     * @return byte[] 指定加密方式为md5后的byte[]
     * @throws NoSuchAlgorithmException     e
     * @throws UnsupportedEncodingException e
     */
    public static byte[] md5(String strSrc, String encoding) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] returnByte = md5.digest(strSrc.getBytes(encoding));
        return returnByte;
    }

    /**
     * @param src src
     * @return src
     * @throws NoSuchAlgorithmException y
     * @author jiangzeyin
     */
    public static String md5ByHex(String src) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = src.getBytes();
        md.reset();
        md.update(b);
        byte[] hash = md.digest();
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (int i = 0; i < hash.length; i++) {
            stmp = Integer.toHexString(hash[i] & 0xFF);
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }

    public static String getFileMD5(File file) throws IOException, NoSuchAlgorithmException {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        byte buffer[] = new byte[1024];
        int len;
        MessageDigest digest = MessageDigest.getInstance("MD5");
        FileInputStream in = new FileInputStream(file);
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        StringBuilder sb = new StringBuilder();
        for (byte t : digest.digest()) {
            String s = Integer.toHexString(t & 0xFF);
            if (s.length() == 1) // 补零
                s = "0" + s;
            sb.append(s);
        }
        String md5 = sb.toString().toUpperCase();
        if (md5.length() != 32)
            throw new RuntimeException(file.getPath() + " 获取md5(" + md5 + ") 长度不是32");
        return md5;
    }
}
