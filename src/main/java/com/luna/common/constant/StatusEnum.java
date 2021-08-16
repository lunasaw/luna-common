package com.luna.common.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 用户状态枚举
 *
 * @author haoxin
 * @date 2021-02-02
 **/
public enum StatusEnum {

    /**
     * 有效
     */
    ENABLE("0", "有效"),

    /**
     * 禁用
     */
    DISABLE("1", "禁用");

    private final String value;

    private final String label;

    StatusEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据匹配value的值获取Label
     *
     * @param value
     * @return
     */
    public static String getLabelByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return StringUtils.EMPTY;
        }
        for (StatusEnum s : StatusEnum.values()) {
            if (value.equals(s.getValue())) {
                return s.getLabel();
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取StatusEnum
     *
     * @param value
     * @return
     */
    public static StatusEnum getStatusEnum(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (StatusEnum s : StatusEnum.values()) {
            if (value.equals(s.getValue())) {
                return s;
            }
        }
        return null;
    }
}
