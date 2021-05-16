package com.luna.common.utils;

/**
 * @author luna@mac
 * @className CountDownUtils.java
 * @description TODO
 * @createTime 2021年01月12日 23:50:00
 */
public class CountDownUtils {


    public static void countDown(long midTime) {

        while (midTime > 0) {
            midTime--;
            long ss = midTime % 60;
            System.out.println("距离下次操作还剩" + ss + "秒");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
