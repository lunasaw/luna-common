package com.luna.common.encrypt;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.luna.common.constant.Constant;

/**
 * @author luna
 */
public class CreateKeyUtils {
    /**
     * 随机生成密码 默认都最小有1个
     * 
     * @param intLength
     * @return
     */
    public static String getRandomKeys(int intLength) {
        return getRandomKeys(intLength, Constant.NUMBER_ONE, Constant.NUMBER_ONE, Constant.NUMBER_ONE,
            Constant.NUMBER_ONE);
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
     * @param intLength 总长度
     * @param numbercaseMax 数字个数
     * @param lowercaseMax 小写字母
     * @param uppercaseMax 大写字母
     * @param strcaseMax 字符
     * @return
     */
    public static String getRandomKeys(int intLength, int numbercaseMax, int lowercaseMax, int uppercaseMax,
        int strcaseMax) {

        StringBuilder retStr; // 生成的密码
        String strTable = Constant.TABLE;
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