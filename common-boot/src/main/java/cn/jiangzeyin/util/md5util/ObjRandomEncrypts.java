package cn.jiangzeyin.util.md5util;

import cn.jiangzeyin.util.util.RandomUtil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * Created by jiangzeyin on 2017/3/7.
 */
public final class ObjRandomEncrypts {


    /**
     * 3-DES&#x52a0;&#x5bc6;
     *
     * @param enKey src &#x8981;&#x8fdb;&#x884c;3-DES&#x52a0;&#x5bc6;&#x7684;byte[]
     * @param src   enKey 3-DES&#x52a0;&#x5bc6;&#x5bc6;&#x94a5;
     * @return byte[] 3-DES&#x52a0;&#x5bc6;&#x540e;&#x7684;byte[]
     * @throws Exception e
     */
    private byte[] Encrypt(byte[] src, byte[] enKey) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(enKey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(RandomEncrypts.KEY);
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(RandomEncrypts.KEY);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(src);
    }


    /**
     * 进行3-DES解密（密钥匙等同于加密的密钥匙）。
     *
     * @param debase64 要进行3-DES解密byte[]
     * @param spKey    分配的SPKEY
     * @return String 3-DES解密后的String
     * @throws Exception e
     */
    private String deCrypt(byte[] debase64, String spKey) throws Exception {
        byte[] key = RandomEncrypts.getEnKey(spKey);
        DESedeKeySpec dks = new DESedeKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(RandomEncrypts.KEY);
        SecretKey sKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(RandomEncrypts.KEY);
        cipher.init(Cipher.DECRYPT_MODE, sKey);
        byte cipherText[] = cipher.doFinal(debase64);
        String strDe = new String(cipherText, RandomEncrypts.encoding);
        return strDe;
    }

    /**
     * 3-DES加密
     *
     * @param src 要进行3-DES加密的String
     * @return String 3-DES加密后的String
     * @throws Exception e
     */
    public String get3DESEncrypt(String src) throws Exception {
        StringBuilder spkey = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            spkey.append(String.valueOf((char) RandomUtil.getCodeAscll()));
        }
        spkey = new StringBuilder(spkey.toString().toLowerCase());
        // System.out.println(spkey);
        // 得到3-DES的密钥匙
        byte[] enKey = RandomEncrypts.getEnKey(spkey.toString());
        // 要进行3-DES加密的内容在进行/"UTF-16LE/"取字节
        byte[] src2 = src.getBytes(RandomEncrypts.encoding);
        // 进行3-DES加密后的内容的字节
        byte[] encryptedData = Encrypt(src2, enKey);
        // 进行3-DES加密后的内容进行BASE64编码
        String base64String = Base64Util.getEncode(encryptedData);
        // BASE64编码去除换行符后
        String base64Encrypt = RandomEncrypts.filter(base64String);

        // 对BASE64编码中的HTML控制码进行转义的过程
        String requestValue = UrlDecode.getURLEncode(base64Encrypt, RandomEncrypts.encoding);
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
    public String get3DESDecrypt(String src) throws Exception {
        String spkey = src.substring(src.length() - 10);
        // 得到3-DES的密钥匙
        src = src.substring(0, src.length() - 10);
        // URLDecoder.decodeTML控制码进行转义的过程
        String URLValue = UrlDecode.getURLDecode(src, RandomEncrypts.encoding);
        // 进行3-DES加密后的内容进行BASE64编码
        // BASE64Decoder base64Decode = new BASE64Decoder();
        byte[] base64DValue = Base64Util.decodeBuffer(URLValue);
        // 要进行3-DES加密的内容在进行/"UTF-16LE/"取字节
        String requestValue = deCrypt(base64DValue, spkey);
        return requestValue;
    }
}
