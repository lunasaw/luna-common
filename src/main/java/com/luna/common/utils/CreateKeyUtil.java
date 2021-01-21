package com.luna.common.utils;

/**
 * 自动生成密码
 * Usage: java CreateKey length count
 * length-密码长度，最小为6
 * count -密码个数
 */

public class CreateKeyUtil {

    public static void main(String[] args) {
        System.out.println(getRandomKeys(9));
    }

    /**
     * 生成指定长度的密码
     * 
     * @param intLength
     * @return
     */
    public static String getRandomKeys(int intLength) {

        String retStr; // 生成的密码
        // String strTable = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz!@#$^&*<>/.,";
        String strTable = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz!@#$";
        // 密码使用符号，可更改

        int len = strTable.length();
        boolean bDone = false;
        // 生成结束标志
        do {
            retStr = "";
            int count = 0;
            // 生成密码中数字的个数
            int count1 = 0;
            // 生成密码中小写字母的个数
            int count2 = 0;
            // 生成密码中符号的个数
            int count3 = 0;
            // 生成密码中大写字母的个数
            for (int i = 0; i < intLength; i++) {
                int intR = (int)Math.floor(Math.random() * len);
                char c = strTable.charAt(intR);
                // 找到指定字符

                // 判断字符类型并计数：数字，字母，符号
                if (('0' <= c) && (c <= '9')) {
                    count++;
                } else if (('a' <= c) && (c <= 'z')) {
                    count1++;
                } else if (('A' <= c) && (c <= 'Z')) {
                    count3++;
                } else {
                    count2++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 1 && count1 >= 1 && count3 >= 1 && count2 >= 1) {
                // 如果符号密码强度，则置结束标志：密码至少包含1个数字，1个小写字母，一个大写字母，1个符号
                bDone = true;
            }
        } while (!bDone);

        return retStr;
    }
}