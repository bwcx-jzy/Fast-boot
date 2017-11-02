//package cn.jiangzeyin.util;
//
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.StringTokenizer;
//
///**
// * 字符串工具类
// *
// * @author jiangzeyin
// */
//public final class StringUtil {
//
//
//    /**
//     * 过滤 &lt; ,   &gt;   , \n 字符的方法。
//     *
//     * @param input 需要过滤的字符
//     * @return 完成过滤以后的字符串
//     */
//    public static String filterHTML(String input) {
//        if (input == null) {
//            return null;
//        }
//        if (input.length() == 0) {
//            return input;
//        }
//        input = input.trim();
//        // input = input.replaceAll("　", "");
//        // input = input.replaceAll("&", "&amp;");
//        input = input.replaceAll("<", "&lt;");
//        input = input.replaceAll(">", "&gt;");
//        // input = input.replaceAll(" ", "&nbsp;");
//        input = input.replaceAll("'", "&#39;");
//        // input = input.replaceAll("\"", "&quot;");
//        //input = input.replaceAll("\n", "<br>");
//
//        // String s = "<:&lt;,>:&gt;, :&nbsp;,':&#39;,\":&quot;,\n:<br>";
//        // StringBuffer sb = new StringBuffer(input);
//        // String[] t = StringToArray(s, ",");
//        // for (String string : t) {
//        // String[] temp = string.split(":");
//        // int i = sb.indexOf(temp[0]);
//        // while (i > -1) {
//        // int oldLen = temp[0].length();
//        // int newLen = temp[1].length();
//        // sb.delete(i, i + oldLen);
//        // sb.insert(i, temp[1]);
//        // i = sb.indexOf(temp[0], i + newLen);
//        // }
//        // }
//        // return sb.toString();
//        return input;
//    }
//
//
//    /**
//     * 编译html
//     *
//     * @param input inp
//     * @return str
//     */
//    public static String compileHtml(String input) {
//        if (input == null) {
//            return null;
//        }
//        if (input.length() == 0) {
//            return input;
//        }
//        input = input.replaceAll("&amp;", "&");
//        input = input.replaceAll("&lt;", "<");
//        input = input.replaceAll("&gt;", ">");
//        input = input.replaceAll("&nbsp;", " ");
//        input = input.replaceAll("&#39;", "'");
//        input = input.replaceAll("&quot;", "\"");
//        return input.replaceAll("<br>", "\n");
//    }
//
//
//    public static int parseInt(String num) {
//        return parseInt(num, 0);
//    }
//
//    public static int parseInt(Object num) {
//        if (num == null)
//            return 0;
//        return parseInt(num.toString(), 0);
//    }
//
//    public static int parseInt(Object obj, int default_) {
//        return parseInt(convertNULL(obj), default_);
//    }
//
//    public static int parseInt(String num, int default_) {
//        if ((num == null) || (num.length() == 0))
//            return default_;
//
//        try {
//            return Integer.parseInt(num);
//        } catch (NumberFormatException ignored) {
//        }
//        return default_;
//    }
//
//    public static long parseLong(String num) {
//        if (num == null)
//            return 0L;
//        try {
//            return Long.parseLong(num);
//        } catch (NumberFormatException ignored) {
//        }
//        return 0L;
//    }
//
//    public static float parseFloat(String num) {
//        if (num == null)
//            return 0.0F;
//        try {
//            return Float.parseFloat(num);
//        } catch (NumberFormatException ignored) {
//        }
//        return 0.0F;
//    }
//
//    public static double parseDouble(String num) {
//        if (num == null)
//            return 0.0D;
//        try {
//            return Double.parseDouble(num);
//        } catch (NumberFormatException ignored) {
//        }
//        return 0.0D;
//    }
//
//    /**
//     * 编码字符串
//     *
//     * @param str str
//     * @return s
//     */
//    public static String getUTF8(String str) {
//        if (str == null)
//            return "";
//        try {
//            return new String(str.getBytes("ISO-8859-1"), "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            return "";
//        }
//    }
//
//
//    public static String convertNULL(String input) {
//        if (input == null)
//            return "";
//        return input.trim().intern();
//    }
//
//    public static String convertNULL(Object input) {
//        if (input == null)
//            return "";
//        return convertNULL(input.toString());
//    }
//
//
//
//}