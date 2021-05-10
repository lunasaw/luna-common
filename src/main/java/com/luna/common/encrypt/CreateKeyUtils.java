package com.luna.common.encrypt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动生成密码
 * Usage: java CreateKey length count
 * length-密码长度，最小为6
 * count -密码个数
 * 
 * @author luna_mac
 */

public class CreateKeyUtils {

    /** 数字个数 */
    public static final int    NUMBER_CASE = 1;
    /** 小写字母 */
    public static final int    LOWER_CASE  = 1;
    /** 大写字母 */
    public static final int    UPPER_CASE  = 1;
    /** 字符 */
    public static final int    STR_CASE    = 1;
    public static final String TABLE       = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz!@#$";
    public static final String TABLE_TWO   = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz!@#$^&*<>/.,";

    public static void main(String[] args) {
        System.out.println(getRandomKeys(10));
    }

    /**
     * 随机生成密码 默认都最小有1个
     * 
     * @param intLength
     * @return
     */
    public static String getRandomKeys(int intLength) {
        return getRandomKeys(intLength, NUMBER_CASE, LOWER_CASE, UPPER_CASE, STR_CASE);
    }

    /**
     * 随机个数的密码
     * 
     * @param intLength
     * @param count
     * @return
     */
    public static List<String> getRandomKeys(int intLength, int count) {
        ArrayList<String> list = Lists.newArrayList();
        for (int i = 0; i < count; i++) {
            list.add(getRandomKeys(intLength));
        }
        return list;
    }

    /**
     * 生成指定长度的密码
     * 
     * @param intLength
     * @return
     */
    public static String getRandomKeys(int intLength, int numbercaseMax, int lowercaseMax, int uppercaseMax,
        int strcaseMax) {

        StringBuilder retStr; // 生成的密码
        String strTable = TABLE;
        // 密码使用符号，可更改

        int len = strTable.length();
        boolean bDone = false;
        // 生成结束标志
        do {
            retStr = new StringBuilder();
            int numbercase = 0;
            // 生成密码中数字的个数
            int lowercase = 0;
            // 生成密码中小写字母的个数
            int strcase = 0;
            // 生成密码中符号的个数
            int uppercase = 0;
            // 生成密码中大写字母的个数
            for (int i = 0; i < intLength; i++) {
                int intR = (int)Math.floor(Math.random() * len);
                char c = strTable.charAt(intR);
                // 找到指定字符

                // 判断字符类型并计数：数字，字母，符号
                if (('0' <= c) && (c <= '9')) {
                    numbercase++;
                } else if (('a' <= c) && (c <= 'z')) {
                    lowercase++;
                } else if (('A' <= c) && (c <= 'Z')) {
                    uppercase++;
                } else {
                    strcase++;
                }
                retStr.append(strTable.charAt(intR));
            }
            if (numbercase >= numbercaseMax && lowercase >= lowercaseMax && uppercase >= uppercaseMax
                && strcase >= strcaseMax) {
                // 如果符号密码强度，则置结束标志：密码至少包含1个数字，1个小写字母，一个大写字母，1个符号
                bDone = true;
            }
        } while (!bDone);

        return retStr.toString();
    }
}