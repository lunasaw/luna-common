package com.luna.common.date;

import org.joda.time.DateTimeConstants;

/**
 * @author Luna
 * @since 2020/1/14
 */
public enum DayWeekEnum {
    SUNDAY(DateTimeConstants.SUNDAY, "星期日"),
    SATURDAY(DateTimeConstants.SATURDAY, "星期六"),
    FRIDAY(DateTimeConstants.FRIDAY, "星期五"),
    THURSDAY(DateTimeConstants.THURSDAY, "星期四"),
    WEDNESDAY(DateTimeConstants.WEDNESDAY, "星期三"),
    TUESDAY(DateTimeConstants.TUESDAY, "星期二"),
    MONDAY(DateTimeConstants.MONDAY, "星期一");

    private int    code;
    private String name;

    private DayWeekEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(int code) {
        for (DayWeekEnum dayWeekEnum : DayWeekEnum.values()) {
            if (dayWeekEnum.code == code) {
                return dayWeekEnum.getName();
            }
        }
        return SUNDAY.getName();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}