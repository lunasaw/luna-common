package com.luna.common.cron;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

public class CronUtil {

    /**
     * 相对于当前时间多少分钟后的执行任务的cron表达式
     * 
     * @param minute
     * @return
     */
    public static String getCronAfterMinutes(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        Date scheduleTime = calendar.getTime();
        return getCron(scheduleTime);
    }

    public static String getCronAfterSeconds(int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, second);
        Date scheduleTime = calendar.getTime();
        return getCron(scheduleTime);
    }

    /***
     * convert Date to cron ,eg. "0 06 10 15 1 ? 2014"
     * 
     * @param date : 时间点
     * @return
     */
    public static String getCron(java.util.Date date) {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }

    private static String formatDateByPattern(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    /**
     * 周期性任务,每X分钟执行一次
     * 
     * @param x
     * @return
     */
    public static String getCronEveryXMinutes(int x) {
        return String.format("0 0/%d * * * ? *", x);
    }

    public static Date formatDateByCron(String cron, long time) {
        try {
            // 如果是周期任务，则设置后面的时间，防止被后台线程拿到
            if (checkCronOneTime(cron)) {
                return getOnceTime(cron);
            } else {
                return getLoopTime(cron, time);
            }

        } catch (Exception e) {
            throw new RuntimeException("cron:" + cron, e);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(TimeUnit.MINUTES.toMicros(20));
        Date nextDate = null;
        for (int i = 0; i < 10; i++) {
            long time = System.currentTimeMillis();
            if (nextDate != null) {
                long nextTime = nextDate.getTime();
                if (time < nextTime) {
                    time = nextTime;
                }
            }

            String expreTime = "10 0 0 * * ? *";
            nextDate = formatDateByCron(expreTime, time);
            System.out.println(nextDate);
        }
    }

    private static Date getOnceTime(String cron) throws ParseException {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        return sdf.parse(cron);
    }

    private static Date getLoopTime(String cron, long time) throws ParseException {
        CronExpression cronExpression = new CronExpression(cron);
        return cronExpression.getNextValidTimeAfter(new Date(time));
    }

    /* 判断用户提交的表达式类型是周期性的还是一次性的 */
    public static boolean checkCronOneTime(String cron) throws RuntimeException {
        if (StringUtils.isBlank(cron)) {
            return false;
        }
        String[] items = cron.split(" ");
        int length = items.length;
        if (items.length < 6) {
            throw new RuntimeException("error cron expression format");
        }
        boolean result = true;
        for (int i = 0; i < length; i++) {
            result &= !containsCommonChar(items[i]);
        }
        return result;
    }

    private static boolean containsCommonChar(String cron) {
        char[] invalidCharList = new char[] {',', '-', '*', '/'};
        if (cron == null) {
            return false;
        }
        for (char chr : invalidCharList) {
            if (cron.indexOf(chr) > -1) {
                return true;
            }
        }
        return false;
    }
}
