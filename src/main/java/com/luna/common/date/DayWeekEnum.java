package com.luna.common.date;

/**
 * @author Luna
 * @since 2020/1/14
 */
public enum DayWeekEnum {
    /** 星期 */
    SUNDAY(0, "星期日"),
    SATURDAY(6, "星期六"),
    FRIDAY(5, "星期五"),
    THURSDAY(4, "星期四"),
    WEDNESDAY(3, "星期三"),
    TUESDAY(2, "星期二"),
    MONDAY(1, "星期一");

    private int    code;
    private String name;

    DayWeekEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DayWeekEnum getNameByCode(int code) {
        for (DayWeekEnum dayWeekEnum : DayWeekEnum.values()) {
            if (dayWeekEnum.code == code) {
                return dayWeekEnum;
            }
        }
        return null;
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