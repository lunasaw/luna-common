package com.luna.common.utils;


/**
 * @author luna
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
