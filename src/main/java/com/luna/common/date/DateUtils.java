package com.luna.common.date;

import com.luna.common.constant.Constant;
import com.luna.common.constant.StrPoolConstant;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class DateUtils {

    public static final String                         FORMAT_YYYY_MM_DD          = "yyyy-MM-dd";

    public static final String                         FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String                         FORMAT_YYYY_MM_DD_HH_MM    = "yyyy-MM-dd HH:mm";

    public static final String                         FORMAT_HH_MM_SS            = "HH:mm:ss";

    public static final String                         FORMAT_YYYYMMDD            = "yyyyMMdd";

    public static final String                         FORMAT_YYYYMMDDHHMMSS      = "yyyyMMddHHmmssSSS";

    public static final String                         FORMAT_CHINESE             = "yyyy年M月d日";

    public static final String                         FORMAT_HH_MM               = "HH:mm";

    public static final String                         FORMAT_YMDHMS              = "yyyyMMdd hh:mm:ss";

    public static final String                         START_TIME                 = "00:00:00";

    public static final String                         END_TIME                   = "23:59:59";

    private static final SimpleDateFormat              SDF_DATE                   =
        new SimpleDateFormat(FORMAT_YYYY_MM_DD);
    private static final SimpleDateFormat              PURE_DATE_FORMAT           =
        new SimpleDateFormat(FORMAT_YYYYMMDD);
    private static final SimpleDateFormat              SDF_DATETIME               =
        new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);
    private static final SimpleDateFormat              SDF_SHORTDATETIME          =
        new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM);
    private static final SimpleDateFormat              SDF_TIME                   =
        new SimpleDateFormat(FORMAT_HH_MM_SS);
    private static final SimpleDateFormat              SDF_SIMPLEDATETIME         =
        new SimpleDateFormat(FORMAT_YYYYMMDD);
    private static final Map<String, SimpleDateFormat> SIMPLE_DATE_FORMATERS      =
        new HashMap<String, SimpleDateFormat>();

    static {
        SIMPLE_DATE_FORMATERS.put(FORMAT_YYYY_MM_DD, SDF_DATE);
        SIMPLE_DATE_FORMATERS.put(FORMAT_YYYY_MM_DD_HH_MM_SS, SDF_DATETIME);
        SIMPLE_DATE_FORMATERS.put(FORMAT_YYYY_MM_DD_HH_MM, SDF_SHORTDATETIME);
        SIMPLE_DATE_FORMATERS.put(FORMAT_HH_MM_SS, SDF_TIME);
        SIMPLE_DATE_FORMATERS.put(FORMAT_YYYYMMDD, SDF_SIMPLEDATETIME);
    }

    private static SimpleDateFormat getCachedDateFormat(String aMask) {
        return SIMPLE_DATE_FORMATERS.get(aMask);
    }

    /**
     * 返回自字符串格式为：yyyy-MM-dd
     */
    public static String formatDate(Date aDate) {
        return format(FORMAT_YYYY_MM_DD, aDate);
    }

    /**
     * 返回自字符串格式为：yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(Date date) {
        return format(FORMAT_YYYY_MM_DD_HH_MM_SS, date);
    }

    /**
     * 返回自字符串格式为：yyyyMMddHHmmssSSS
     */
    public static String formatDateTimeStr(Date date) {
        return format(FORMAT_YYYYMMDDHHMMSS, date);
    }

    /**
     * 返回自字符串格式为：HH:mm:ss
     */
    public static String formatTime(Date date) {
        return format(FORMAT_HH_MM_SS, date);
    }

    /**
     * 返回自定义格式字符串
     */
    public static String format(String aMask, Date aDate) {
        if (aDate == null) {
            return null;
        } else {
            SimpleDateFormat sd = getCachedDateFormat(aMask);
            if (sd == null) {
                sd = new SimpleDateFormat(aMask);
                return sd.format(aDate);
            }
            synchronized (sd) {
                return sd.format(aDate);
            }
        }
    }

    /**
     * 只支持格式yyyy-MM-dd
     */
    public static Date parseDate(String strDate) {
        try {
            return parse(FORMAT_YYYY_MM_DD, strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 只支持格式yyyy-MM-dd HH:mm:ss
     */
    public static Date parseDateTime(String strDate) {
        try {
            return parse(FORMAT_YYYY_MM_DD_HH_MM_SS, strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 只支持格式yyMMdd
     */
    public static Date parseDateDay(String strDate) {
        try {
            return parse(FORMAT_YYYYMMDD, strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 只支持格式HH:mm:ss
     */
    public static Date parseTime(String timeOnly) {
        timeOnly = formatDate(new Date()) + StrPoolConstant.BLANK + timeOnly;
        try {
            return parse(FORMAT_YYYY_MM_DD_HH_MM_SS, timeOnly);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 支持自定义格式转换
     */
    public static Date parse(String aMask, String strDate) throws ParseException {
        if (strDate == null) {
            return null;
        } else {
            SimpleDateFormat sd = getCachedDateFormat(aMask);
            if (sd == null) {
                sd = new SimpleDateFormat(aMask);
                return sd.parse(strDate);
            }
            synchronized (sd) {
                return sd.parse(strDate);
            }
        }
    }

    /**
     * 年4位，月、日、时、分、秒各2位，毫秒3位，支持从右边删除任意部分，如：只有年月日，或者只有年月等
     */
    public static Date parse(String strDate) throws ParseException {
        if (strDate == null) {
            return null;
        } else {
            strDate = strDate.replace("-", "").replace(".", "").replace("/", "").replace("\\", "").replace(" ", "")
                .replace(":", "");
            String formate = FORMAT_YYYYMMDDHHMMSS;
            formate = formate.substring(0, strDate.length());
            return new SimpleDateFormat(formate).parse(strDate);
        }
    }

    /**
     * 毫秒数转为字符串 按照指定格式转换
     *
     * @param timestamp
     * @return
     */
    public static String format(Long timestamp, String pattern) {
        String dateStr = "";
        if (null == timestamp || timestamp.longValue() < 0) {
            return dateStr;
        }
        try {
            Date date = new Date(timestamp);
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.format(date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取日期的开始时间
     *
     * @param dateStr 日期Str
     * @return
     */
    public static Date getDateStart(String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_YMDHMS);
        String subStr = dateStr.substring(0, 8);
        try {
            return simpleDateFormat.parse(subStr + " " + START_TIME);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取日期的结束时间
     *
     * @param dateStr 日期Str
     * @return
     */
    public static Date getDateEnd(String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_YMDHMS);
        String subStr = dateStr.substring(0, 8);
        try {
            return simpleDateFormat.parse(subStr + " " + END_TIME);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 获取当前时间戳，单位秒
     *
     * @return
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前时间戳，单位毫秒
     *
     * @return
     */
    public static long getCurrentTimestampMs() {
        return System.currentTimeMillis();
    }

    public static Date getCurrentDate() {
        return new Date(getCurrentTimestampMs());
    }

    public static String getTimeInteval(Date date) {
        if (null == date) {
            return "";
        }
        int hours, minutes, seconds;
        long timeMillSeconds = System.currentTimeMillis() - date.getTime();
        hours = (int)(timeMillSeconds / (60 * 60 * 1000));
        timeMillSeconds = timeMillSeconds - (hours * 60 * 60 * 1000);
        minutes = (int)(timeMillSeconds / (60 * 1000));
        timeMillSeconds = timeMillSeconds - (minutes * 60 * 1000);
        seconds = (int)(timeMillSeconds / 1000);
        String inteval = "";
        if (hours > 0) {
            inteval = hours + "小时" + minutes + "分" + seconds + "秒";
        } else if (minutes > 0) {
            inteval = minutes + "分" + seconds + "秒";
        } else {
            inteval = seconds + "秒";
        }
        return inteval;
    }

    public static Date addYears(Date date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    public static Date addWeeks(Date date, int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    public static Date addDays(Date date, int amount) {
        // return add(date, Calendar.MINUTE, amount);
        return add(date, Calendar.DAY_OF_YEAR, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    public static Date addSeconds(Date date, int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    public static String getTodayString() {
        return format("yyyyMMdd", new Date());
    }

    private static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(calendarField, amount);
            return c.getTime();
        }
    }

    public static String nextDate(String strdate) throws ParseException {
        Date temp = parse(FORMAT_YYYY_MM_DD, strdate);
        Date next = new Date(temp.getTime() + 24 * 3600 * 1000);
        return formatDate(next);
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static String appendTime(String date) {
        int eight = 8, ten = 10;
        if (date != null) {
            // 2014-07-01
            if (date.length() >= eight && date.length() <= ten
                && date.indexOf(StrPoolConstant.DASHED) != date.lastIndexOf(StrPoolConstant.DASHED)) {
                date = date + " 23:59:59.999";
            }
        }
        return date;
    }

    /**
     * 获取凌晨00:00:00
     *
     * @param date
     * @return
     * @author huayi
     */
    public static Date getMorning(Date date) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        dateCalendar.set(Calendar.HOUR_OF_DAY, 0);
        dateCalendar.set(Calendar.MINUTE, 0);
        dateCalendar.set(Calendar.SECOND, 0);
        dateCalendar.set(Calendar.MILLISECOND, 0);

        return dateCalendar.getTime();
    }

    /**
     * 获取0点时间的字符串
     * 
     * @param date
     * @return
     */
    public static String getMorningStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD + StrPoolConstant.BLANK + START_TIME);
        return sdf.format(date);
    }

    /**
     * 获取23.59.59秒的时间字符串
     * 
     * @param date
     * @return
     */
    public static String getNightStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD + StrPoolConstant.BLANK + END_TIME);
        return sdf.format(date);
    }

    /**
     * 获得 某天的23:59:59
     *
     * @param date
     * @return
     * @author huayi
     */
    public static Date getNight(Date date) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        dateCalendar.set(Calendar.HOUR_OF_DAY, 23);
        dateCalendar.set(Calendar.MINUTE, 59);
        dateCalendar.set(Calendar.SECOND, 59);
        dateCalendar.set(Calendar.MILLISECOND, 0);

        return dateCalendar.getTime();
    }

    /**
     * 取得日期是某年的第几周
     */
    public static int getWeekOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        return weekOfYear;
    }

    /**
     * 得两个日期之间的相差多少天
     */
    public static long getDaysBetween(Date beginDate, Date endDate) {
        // 86400000=3600*24*1000
        long daysBetween = (beginDate.getTime() - endDate.getTime() + 1000000) / 86400000;
        // 用立即数，减少乘法计算的开销
        return daysBetween;
    }

    /**
     * 判断两个日期相差的时长<br>
     * 返回 给定单位的时长差
     *
     * @param unit 相差的单位：相差 天{@link DateUnit#DAY}、小时{@link DateUnit#HOUR} 等
     * @return 时长差
     */
    public static long between(Date end, Date begin, DateUnit unit) {
        long diff = end.getTime() - begin.getTime();
        return diff / unit.getMillis();
    }

    /**
     * 根据日期取得星期几 weekIndex = 0 表示星期日，依次类推
     */
    public static int getWeekDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekIndex < 0) {
            weekIndex = 0;
        }
        return weekIndex;
    }

    /**
     * 设置日期到那一周的周几，weekIndex = 0 表示星期日，依次类推
     */
    public static Date setWeekDay(Date date, int weekIndex) {
        int oldWeekIndex = getWeekDay(date);
        if (oldWeekIndex == 0) {
            oldWeekIndex = 7;
        }

        if (weekIndex == 0) {
            weekIndex = 7;
        }

        return addDays(date, weekIndex - oldWeekIndex);
    }

    public static boolean firstDayInMonth() {
        String todayString = DateUtils.getTodayString();
        return todayString.endsWith("01");
    }

    /**
     * date是否是给定的第x天
     * 
     * @param date date
     * @param day day
     * @return boolean
     */
    public static boolean isDayOfMonth(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return dayOfMonth == day;
    }

    /**
     * 获取当月0点毫秒时间
     * 
     * @return long
     */
    public static long getMonthBeginStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 当前年份
     * 
     * @return
     */
    public static int thisYear() {
        return getYear(getCurrentDate());
    }

    /**
     * 是否闰年
     *
     * @param year 年
     * @return 是否闰年
     */
    public static boolean isLeapYear(int year) {
        return new GregorianCalendar().isLeapYear(year);
    }
}