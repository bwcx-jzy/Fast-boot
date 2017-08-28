package cn.jiangzeyin.util.util.date;

import cn.jiangzeyin.util.util.StringUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间工具类
 *
 * @author jiangzeyin
 */
public final class DateUtil {

    /**
     * @param pattern p
     * @param date    date
     * @return str
     * @author jiangzeyin
     */
    public static String FormatTimeStamp(String pattern, long date) {
        if (pattern == null || pattern.length() == 0)
            pattern = "yyyy-MM-dd HH:mm:ss";
        Calendar nowDate = new GregorianCalendar();
        nowDate.setTimeInMillis(date * 1000L);
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(nowDate.getTime());
    }

    /**
     * @param pattern p
     * @param date    date
     * @return str
     * @author jiangzeyin
     */
    public static String FormatTime(String pattern, long date) {
        if (pattern == null || pattern.length() == 0)
            pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 获取当前时间
     *
     * @return str
     * @author jiangzeyin
     */
    public static String getCurrentTime() {
        return FormatTimeStamp("", System.currentTimeMillis() / 1000L);
    }

    public static long getCurrentUnixTime() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * @param startDate start
     * @param startTime start
     * @return long
     * @author jiangzeyin
     */
    public static long getFromTime(String startDate, String startTime) {
        Calendar nowDate;
        long fromtime = 0L;
        if (StringUtil.isEmpty(startTime)) {
            startTime = "00:00";
        }
        if (!startDate.equals("")) {
            String[] s = startDate.split("-");
            nowDate = new GregorianCalendar();
            nowDate.set(Calendar.DATE, Integer.parseInt(s[2]));
            nowDate.set(Calendar.MONTH, Integer.parseInt(s[1]) - 1);
            nowDate.set(Calendar.YEAR, Integer.parseInt(s[0]));

            String[] t = startTime.split(":");
            nowDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(t[0]));
            nowDate.set(Calendar.MINUTE, Integer.parseInt(t[1]));
            nowDate.set(Calendar.SECOND, 0);
            fromtime = nowDate.getTimeInMillis() / 1000L;
        }
        return fromtime;
    }

    /**
     * 格式化时间 10位
     *
     * @param date date
     * @return date
     * @author jiangzeyin
     */
    public static Date FormatTimeStamp(long date) {
        Calendar nowDate = new GregorianCalendar();
        nowDate.setTimeInMillis(date * 1000L);
        return nowDate.getTime();
    }

    /**
     * 获取当月的 天数
     *
     * @return int
     */
    public static int getCurrentMonthDay() {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 根据年 月 获取对应的月份 天数
     *
     * @param year  y
     * @param month m
     * @return int
     */
    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 根据日期 找到对应日期的 星期
     *
     * @param date date
     * @return str
     */
    public static String getDayOfWeekByDate(String date) {
        String dayOfweek = "-1";
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = myFormatter.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("E");
            String str = formatter.format(myDate);
            dayOfweek = str;

        } catch (Exception e) {
            System.out.println("错误!");
        }
        return dayOfweek;
    }

    public static boolean checkTimeDiff(long data, long d) {
        data = data * 1000L;
        long diff = System.currentTimeMillis() - data;
        if (diff > d)
            return true;
        return false;
    }

    /**
     * @param pattern p
     * @param time    time
     * @return long
     * @throws ParseException pe
     */
    public static long fromDate(String pattern, String time) throws ParseException {
        if (pattern == null || pattern.length() == 0)
            pattern = "yyyy-MM-dd HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date date = df.parse(time);
        return date.getTime();
    }

    /**
     * 得到几天前的时间
     *
     * @param d   d
     * @param day day
     * @return date
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天前的时间
     *
     * @param pattern 显示格式(如："yyyy-MM-dd HH:mm:ss")
     * @param d       日期
     * @param day     几天前
     * @return str
     * @author xiangzhongbao
     */
    public static String getDateBefore(String pattern, Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        if (pattern == null || pattern.length() == 0)
            pattern = "yyyy-MM-dd HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(now.getTime());
    }

    /**
     * 得到几天后的时间
     *
     * @param d   d
     * @param day day
     * @return date
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 格式化日期(yyyy-MM-dd HH:mm:ss)
     *
     * @param date date
     * @return str
     */
    public static String getFormat(Date date) {
        if (date == null) {
            return "暂时无时间记录";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 把毫秒转化成日期
     *
     * @param dateFormat (日期格式，例如：MM/ dd/yyyy HH:mm:ss)
     * @param millSec    (毫秒数)
     * @return str
     */
    public static String transferLongToDate(String dateFormat, long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    /**
     * 将时间字符串转换为10位时间戳
     *
     * @param s s
     * @return int
     * @throws ParseException pe
     */
    public static int dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        return (int) (date.getTime() / 1000L);
    }


    public static int getDateMonth(long dateNum) {
        String date = transferLongToDate("yyyy-MM-dd HH:mm:ss", dateNum);
        int monthIndex = StringUtil.parseInt(date.substring(5, 7));
        return monthIndex;
    }

    /**
     * 根据时间戳获取年份
     *
     * @param dateNum d
     * @return int
     */
    public static int getDateYear(long dateNum) {
        String date = transferLongToDate("yyyy-MM-dd HH:mm:ss", dateNum);
        int monthIndex = StringUtil.parseInt(date.substring(0, 4));
        if (monthIndex <= 0)
            return 0;
        return monthIndex;
    }

    /**
     * 根据年月日获取当天00:00的时间戳
     *
     * @param day   day
     * @param month m
     * @param year  y
     * @return long
     * @throws ParseException pe
     */
    public static long getTimeStampByYearMonthAndDay(int year, int month, int day) throws ParseException {
        String year_ = year + "";
        String month_ = "";
        String day_ = "";
        if (month < 10) {
            month_ = "0" + month;
        } else {
            month_ = "" + month;
        }
        if (day < 10) {
            day_ = "0" + day;
        } else {
            day_ = "" + day;
        }
        String dateTime = year_ + month_ + day_;
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = df.parse(dateTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis() / 1000L;
    }

}
