package com.luna.common.date;

import com.luna.common.constant.StrPoolConstant;
import com.luna.common.text.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
     * 返回自字符串格式为：yyyy-MM-dd
     */
    public static String formatDate(long time) {
        return formatDate(new Date(time));
    }

    /**
     * 返回自字符串格式为：yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(long time) {
        return formatDateTime(new Date(time));
    }

    /**
     * 返回自字符串格式为：yyyyMMddHHmmssSSS
     */
    public static String formatDateTimeStr(long time) {
        return formatDateTimeStr(new Date(time));
    }

    /**
     * 返回自字符串格式为：HH:mm:ss
     */
    public static String formatTime(long time) {
        return formatTime(new Date(time));
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
        if (ObjectUtils.isEmpty(timestamp)) {
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
        timeMillSeconds = timeMillSeconds - ((long)hours * 60 * 60 * 1000);
        minutes = (int)(timeMillSeconds / (60 * 1000));
        timeMillSeconds = timeMillSeconds - ((long)minutes * 60 * 1000);
        seconds = (int)(timeMillSeconds / 1000);
        String inteval;
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
        return format(FORMAT_YYYYMMDD, new Date());
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
     * @param date 任意时间
     * @return    Date
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
     * @param date 任意时间
     * @return String
     */
    public static String getMorningStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD + StrPoolConstant.BLANK + START_TIME);
        return sdf.format(date);
    }

    /**
     * 获取23.59.59秒的时间字符串
     * 
     * @param date 任意时间当天
     * @return String
     */
    public static String getNightStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD + StrPoolConstant.BLANK + END_TIME);
        return sdf.format(date);
    }

    /**
     * 获得 某天的23:59:59
     *
     * @param date 任意时间
     * @return Date
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
     * @param date 任意时间
     * @return int
     */
    public static int getWeekOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
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
        return getMonthBeginStamp(new Date());
    }

    /**
     * 获取当月月底毫秒时间
     *
     * @return long
     */
    public static long getMonthEndStamp() {
        return getLastStampOfMonth(new Date());
    }

    /**
     * 获取指定时间当月0点毫秒时间
     *
     * @return long
     */
    public static long getMonthBeginStamp(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 获取指定时间当月0点毫秒时间
     *
     * @return long
     */
    public static Date getMonthBeginDateTime(Date date) {
        return new Date(getMonthBeginStamp(date));
    }

    /**
     * 获得某月的最后一天的最后时刻
     *
     * @param date 要获取的月份
     * @return long
     */
    public static long getLastStampOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getMonthBeginStamp(date));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime().getTime();
    }

    /**
     * 获得某月的最后一天的最后时间
     *
     * @param date 要获取的月份
     * @return Date
     */
    public static Date getLastDateTimeOfMonth(Date date) {
        return new Date(getLastStampOfMonth(date));
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

    /**
     * 当前季度的开始时间
     *
     * @return Date
     */
    public static Date getQuarterStartTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        if (currentMonth <= 3) {
            c.set(Calendar.MONTH, 0);
        } else if (currentMonth <= 6) {
            c.set(Calendar.MONTH, 3);
        } else if (currentMonth <= 9) {
            c.set(Calendar.MONTH, 6);
        } else if (currentMonth <= 12) {
            c.set(Calendar.MONTH, 9);
        }
        c.set(Calendar.DATE, 1);
        return getMorning(c.getTime());
    }

    /**
     * 当前季度的开始时间
     *
     * @return long
     */
    public static long getQuarterStartStamp(Date date) {
        return getQuarterStartTime(date).getTime();
    }

    /**
     * 当前季度的结束时间
     *
     * @return Date
     */
    public static Date getQuarterEndTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        if (currentMonth <= 3) {
            c.set(Calendar.MONTH, 2);
            c.set(Calendar.DATE, 31);
        } else if (currentMonth <= 6) {
            c.set(Calendar.MONTH, 5);
            c.set(Calendar.DATE, 30);
        } else if (currentMonth <= 9) {
            c.set(Calendar.MONTH, 8);
            c.set(Calendar.DATE, 30);
        } else if (currentMonth <= 12) {
            c.set(Calendar.MONTH, 11);
            c.set(Calendar.DATE, 31);
        }
        return getNight(c.getTime());
    }

    /**
     * 当前季度的结束时间
     *
     * @return long
     */
    public static long getQuarterEndStamp(Date date) {
        return getQuarterEndTime(date).getTime();
    }

    /**
     * 获取当前日期所属季度开始结束时间
     *
     * @param date 任意时间
     * @return
     */
    public static Pair<Long, Long> quarterTime(Date date) {
        return Pair.of(DateUtils.getQuarterStartStamp(date), DateUtils.getQuarterEndStamp(date));
    }

    /**
     * 获取日期所属季度开始结束时间
     *
     * @return
     */
    public static Pair<Long, Long> quarterTime() {
        return Pair.of(DateUtils.getQuarterStartStamp(new Date()), DateUtils.getQuarterEndStamp(new Date()));
    }

    /**
     * 当前年的开始时间
     *
     * @return Date
     */
    public static Date getYearStartTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        return getMorning(c.getTime());
    }

    /**
     * 当前年的开始时间
     *
     * @return long
     */
    public static long getYearStartStamp(Date date) {
        return getYearStartTime(date).getTime();
    }

    /**
     * 当前年的结束时间
     *
     * @return Date
     */
    public static Date getYearEndTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DATE, 31);
        return getNight(c.getTime());
    }

    /**
     * 当前年的结束时间
     *
     * @return long
     */
    public static long getYearEndStamp(Date date) {
        return getYearEndTime(date).getTime();
    }

    /**
     * 获得本周的第一天，周一
     *
     * @return Date
     */
    public static Date getWeekDayStartTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekday = c.get(Calendar.DAY_OF_WEEK) - 2;
        c.add(Calendar.DATE, -weekday);
        return getMorning(c.getTime());
    }

    /**
     * 获得本周的第一天，周一
     *
     * @return long
     */
    public static long getWeekDayStartStamp(Date date) {
        return getWeekDayStartTime(date).getTime();
    }

    /**
     * 获得本周的最后一天，周日
     *
     * @return Date
     */
    public static Date getWeekDayEndTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DATE, 8 - weekday);
        return getNight(c.getTime());
    }

    /**
     * 获得本周的最后一天，周日
     *
     * @return long
     */
    public static long getWeekDayEndStamp(Date date) {
        return getWeekDayEndTime(date).getTime();
    }

    /**
     * 获取前/后半年的开始时间
     *
     * @return date
     */
    public static Date getHalfYearStartTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        if (currentMonth <= 6) {
            c.set(Calendar.MONTH, 0);
        } else if (currentMonth <= 12) {
            c.set(Calendar.MONTH, 6);
        }
        c.set(Calendar.DATE, 1);
        return getMorning(c.getTime());
    }

    /**
     * 获取前/后半年的开始时间
     *
     * @return date
     */
    public static long getHalfYearStartStamp(Date date) {
        return getHalfYearStartTime(date).getTime();
    }

    /**
     * 获取前/后半年的结束时间
     *
     * @return date
     */
    public static Date getHalfYearEndTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        if (currentMonth <= 6) {
            c.set(Calendar.MONTH, 5);
            c.set(Calendar.DATE, 30);
        } else if (currentMonth <= 12) {
            c.set(Calendar.MONTH, 11);
            c.set(Calendar.DATE, 31);
        }
        return getNight(c.getTime());
    }

    /**
     * 获取前/后半年的结束时间
     *
     * @return date
     */
    public static long getHalfYearEndStamp(Date date) {
        return getHalfYearEndTime(date).getTime();
    }

    /**
     * 获取间隔 1 天 的时间列表
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return List<Long>
     */
    public static List<Long> getBetweenWithDay(Date startDate, Date endDate) {
        return dateBetween(startDate.getTime(), endDate.getTime(), 86400000L);
    }

    /**
     * 获取间隔 1 周 的时间列表
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return List<Long>
     */
    public static List<Long> getBetweenWithWeek(Date startDate, Date endDate) {
        return dateBetween(startDate.getTime(), endDate.getTime(), 604800000L);
    }

    /**
     * 获取间隔 1月 (30.44 天) 的时间列表
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return List<Long>
     */
    public static List<Long> getBetweenWithMonth(Date startDate, Date endDate) {
        return dateBetween(startDate.getTime(), endDate.getTime(), 2629743000L);
    }

    /**
     * 获取间隔 1年 (365.24 天) 的时间列表
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return List<Long>
     */
    public static List<Long> getBetweenWithYear(Date startDate, Date endDate) {
        return dateBetween(startDate.getTime(), endDate.getTime(), 31556736000L);
    }

    /**
     * 获取两个时间直接指定间隔天数列表
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param interval 时间间隔
     * @return List<Long>
     */
    public static List<Long> dateBetween(Long startDate, Long endDate, Long interval) {
        List<Long> list = new ArrayList<>();
        // 定义一个一天的时间戳时长
        long oneDay = interval;
        long time = startDate;
        // 循环得出
        while (time <= endDate) {
            list.add(time);
            time += oneDay;
        }
        return list;
    }
}