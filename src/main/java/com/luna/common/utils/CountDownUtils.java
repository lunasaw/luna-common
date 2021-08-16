package com.luna.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luna
 */
public class CountDownUtils {

    private static final Logger log = LoggerFactory.getLogger(CountDownUtils.class);

    public static void countDown(long midTime) {
        while (midTime > 0) {
            midTime--;
            long ss = midTime % 60;
            log.info("距离下次操作还剩" + ss + "秒");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
