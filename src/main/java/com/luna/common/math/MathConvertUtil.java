package com.luna.common.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;

public class MathConvertUtil {

    public static boolean asBoolean(Object obj, boolean defaultBoolean) {
        if (obj == null)
            return defaultBoolean;
        try {
            return Boolean.getBoolean(obj.toString());
        } catch (Exception ex) {
            return defaultBoolean;
        }
    }

    public static int asInt(String entryStr) {
        if (StringUtils.isEmpty(entryStr)) {
            return 0;
        }
        try {
            return Integer.parseInt(entryStr);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static Integer asInteger(String entryStr) {
        if (StringUtils.isEmpty(entryStr)) {
            return null;
        }
        try {
            return Integer.parseInt(entryStr);
        } catch (Exception ex) {
            return null;
        }
    }

    public static long asLong(String entryStr) {
        if (StringUtils.isEmpty(entryStr)) {
            return 0L;
        }
        try {
            return Long.parseLong(entryStr);
        } catch (Exception ex) {
            return 0L;
        }
    }

    /**
     * 这里保留2未精度
     * 入参500，出参5
     * 
     * @param entryStr
     * @return
     */
    public static double longConvert2Double(long entryStr) {
        try {
            return BigDecimal.valueOf(entryStr).divide(
                BigDecimal.valueOf(100), 2,
                RoundingMode.HALF_UP).doubleValue();
        } catch (Exception ex) {
            return 0L;
        }
    }

    /**
     * 转换成精确到2位精度
     *
     * @param entryStr
     * @return
     */
    public static long doubleConvert2Long(double entryStr) {
        try {
            return BigDecimal.valueOf(entryStr).multiply(
                BigDecimal.valueOf(100)).longValue();
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static boolean asBoolean(String entryStr) {
        try {
            return Boolean.parseBoolean(entryStr);
        } catch (Exception ex) {
            return false;
        }
    }

    public static Boolean asBooleanObject(String entryStr) {
        if (StringUtils.isEmpty(entryStr)) {
            return null;
        }
        try {
            return Boolean.parseBoolean(entryStr);
        } catch (Exception ex) {
            return false;
        }
    }

    public static String asString(Object entryStr) {
        if (null == entryStr)
            return "";
        return entryStr.toString();
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
       
    }

}
