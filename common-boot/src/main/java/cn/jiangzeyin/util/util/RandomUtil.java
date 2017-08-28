package cn.jiangzeyin.util.util;

import cn.jiangzeyin.util.md5util.MD5Utils;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author jiangzeyin
 */
public final class RandomUtil {
    public static Random rand = new Random();

    /**
     * 获取随机
     *
     * @return int
     * @author jiangzeyin
     */
    public static int getRandomCode() {
        return getRandomCode(4);
    }

    /**
     * 生成指定长度随机数
     *
     * @param length leg
     * @return int
     * @author jiangzeyin
     */
    public static int getRandomCode(int length) {
        int min = 1;
        for (int i = 0; i < length; i++) {
            min *= 10;
        }
        return rand.nextInt(min);
    }

    public static String shortStr(String url) throws NoSuchAlgorithmException {
        // 可以自定义生成 MD5 加密字符传前的混合 KEY
        String key = "优赚宝";
        // 要使用生成 URL 的字符
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        // 对传入网址进行 MD5 加密
        String hex = MD5Utils.md5ByHex(key + url);

        String resUrl = "";
        for (int i = 0; i < 1; i++) {

            // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);

            // 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 ,
            // 如果不用long ，则会越界
            long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
            StringBuilder outChars = new StringBuilder();
            for (int j = 0; j < 6; j++) {
                // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
                long index = 0x0000003D & lHexLong;
                // 把取得的字符相加
                outChars.append(chars[(int) index]);
                // 每次循环按位右移 5 位
                lHexLong = lHexLong >> 5;
            }
            // 把字符串存入对应索引的输出数组
            resUrl = outChars.toString();
        }
        return resUrl;
    }

    /**
     * 随机生成字母数字
     *
     * @param length leg
     * @return str
     * @author jiangzeyin
     */
    public static String getBoundry(int length) {
        StringBuilder _sb = new StringBuilder();
        for (int t = 1; t <= length; t++) {
            long time = System.currentTimeMillis() + getRandomCode();
            if (time % 3 == 0) {
                _sb.append((char) time % 9);
            } else if (time % 3 == 1) {
                _sb.append((char) (65 + time % 26));
            } else {
                _sb.append((char) (97 + time % 26));
            }
        }
        return _sb.toString();
    }

    /**
     * 获取随机的ascll
     *
     * @return r
     * @author jiangzeyin
     */
    public static int getCodeAscll() {
        int i = 65;
        while (true) {
            i = rand.nextInt(41) + 50;
            if (i >= 58 && i < 65)
                continue;
            if (i == 79 || i == 73)
                continue;
            break;
        }
        return i;
    }
}
