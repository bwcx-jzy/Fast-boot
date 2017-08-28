package cn.jiangzeyin.util.md5util;


import cn.jiangzeyin.util.util.RandomUtil;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * 加密解密工具类
 *
 * @author jiangzeyin
 */
public final class RandomEncrypts {
    public static final String encoding = "UTF-8";
    static final String KEY = "DESede";
    private static SecretKeyFactory keyFactory;
    private static Cipher cipher;

    static {
        try {
            keyFactory = SecretKeyFactory.getInstance(KEY);
            cipher = Cipher.getInstance(KEY);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到3-DES的密钥匙 根据接口规范，密钥匙为24个字节，md5加密出来的是16个字节，因此后面补8个字节的0
     *
     * @param spKey 原始的SPKEY
     * @return byte[] 指定加密方式为md5后的byte[]
     * @throws UnsupportedEncodingException e
     * @throws NoSuchAlgorithmException     e
     */
    public static byte[] getEnKey(String spKey) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] desKey1 = MD5Utils.md5(spKey, encoding);
        byte[] desKey = new byte[24];
        int i = 0;
        while (i < desKey1.length && i < 24) {
            desKey[i] = desKey1[i];
            i++;
        }
        if (i < 24) {
            desKey[i] = 0;
            i++;
        }
        return desKey;
    }

    /**
     * 3-DES&#x52a0;&#x5bc6;
     *
     * @param enKey src &#x8981;&#x8fdb;&#x884c;3-DES&#x52a0;&#x5bc6;&#x7684;byte[]
     * @param src   enKey 3-DES&#x52a0;&#x5bc6;&#x5bc6;&#x94a5;
     * @return byte[] 3-DES&#x52a0;&#x5bc6;&#x540e;&#x7684;byte[]
     * @throws InvalidKeySpecException   e
     * @throws NoSuchPaddingException    e
     * @throws NoSuchAlgorithmException  e
     * @throws BadPaddingException       e
     * @throws IllegalBlockSizeException e
     * @throws InvalidKeyException       e
     */

    private static byte[] Encrypt(byte[] src, byte[] enKey) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        DESedeKeySpec dks = new DESedeKeySpec(enKey);

        SecretKey key = keyFactory.generateSecret(dks);

        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(src);
        return encryptedData;
    }


    /**
     * 去掉字符串的换行符号 base64编码3-DES的数据时，得到的字符串有换行符号 ，一定要去掉，否则uni-wise平台解析票根不会成功，
     * 提示“sp验证失败”。在开发的过程中，因为这个问题让我束手无策， 一个朋友告诉我可以问联通要一段加密后 的文字，然后去和自己生成的字符串比较，
     * 这是个不错的调试方法。我最后比较发现我生成的字符串唯一不同的 是多了换行。 我用c#语言也写了票根请求程序，没有发现这个问题。
     *
     * @param str str
     * @return str
     */
    public static String filter(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            int asc = str.charAt(i);
            if (asc != 10 && asc != 13)
                sb.append(str.subSequence(i, i + 1));
        }
        String output = new String(sb);
        return output;
    }

    /**
     * 进行3-DES解密（密钥匙等同于加密的密钥匙）。
     *
     * @param debase64 要进行3-DES解密byte[]
     * @param spKey    分配的SPKEY
     * @return String 3-DES解密后的String
     * @throws Exception e
     */
    private static String deCrypt(byte[] debase64, String spKey) throws Exception {
        //Cipher cipher = Cipher.getInstance("DESede");
        byte[] key = getEnKey(spKey);
        DESedeKeySpec dks = new DESedeKeySpec(key);
        //SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey sKey = keyFactory.generateSecret(dks);

        cipher.init(Cipher.DECRYPT_MODE, sKey);
        byte ciphertext[] = cipher.doFinal(debase64);
        String strDe = new String(ciphertext, encoding);
        return strDe;
    }

    /**
     * 3-DES加密
     *
     * @param src 要进行3-DES加密的String
     * @return String 3-DES加密后的String
     * @throws Exception e
     */

    public static String get3DESEncrypt(String src) throws Exception {
        StringBuilder spkey = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            spkey.append(String.valueOf((char) RandomUtil.getCodeAscll()));
        }
        spkey = new StringBuilder(spkey.toString().toLowerCase());
        // System.out.println(spkey);
        // 得到3-DES的密钥匙
        byte[] enKey = getEnKey(spkey.toString());
        // 要进行3-DES加密的内容在进行/"UTF-16LE/"取字节
        byte[] src2 = src.getBytes(encoding);
        // 进行3-DES加密后的内容的字节
        byte[] encryptedData = Encrypt(src2, enKey);

        // 进行3-DES加密后的内容进行BASE64编码
        String base64String = Base64Util.getEncode(encryptedData);
        // BASE64编码去除换行符后
        String base64Encrypt = filter(base64String);

        // 对BASE64编码中的HTML控制码进行转义的过程
        String requestValue = UrlDecode.getURLEncode(base64Encrypt, encoding);
        // System.out.println(requestValue);
        return requestValue + spkey;
    }

    /**
     * 3-DES解密
     *
     * @param src 要进行3-DES解密的String
     * @return String 3-DES加密后的String
     * @throws Exception e
     */
    public static String get3DESDecrypt(String src) throws Exception {
        String spkey = src.substring(src.length() - 10);
        // 得到3-DES的密钥匙
        src = src.substring(0, src.length() - 10);
        // URLDecoder.decodeTML控制码进行转义的过程
        String URLValue = UrlDecode.getURLDecode(src, encoding);
        // 进行3-DES加密后的内容进行BASE64编码
        // BASE64Decoder base64Decode = new BASE64Decoder();
        byte[] base64DValue = Base64Util.decodeBuffer(URLValue);
        // 要进行3-DES加密的内容在进行/"UTF-16LE/"取字节
        String requestValue = deCrypt(base64DValue, spkey);
        return requestValue;
    }

}