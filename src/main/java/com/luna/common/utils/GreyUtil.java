package com.luna.common.utils;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * @author luna
 * @date 2024/4/8
 */
public class GreyUtil {

    public static volatile Integer greySwitch;      // 灰度状态

    public static volatile Integer greySwitchValue; // 灰度值

    public static boolean grey(String key, Integer greySwitch) {
        return greySwitch == 1 && grey(key);
    }

    public static boolean grey(String key) {
        if (greySwitchValue == null) {
            greySwitchValue = 100;
        }
        return hashCode(key) % 100 <= greySwitchValue - 1;
    }

    public static long hashCode(String str) {
        CRC32 crc32 = new CRC32();
        crc32.update(ByteBuffer.wrap(str.getBytes()));
        long value = crc32.getValue();
        return Math.abs(value);
    }

    public static void main(String[] args) {
        greySwitchValue = 100;
        int count = 0;
        for (int i = 0; i < 1000; i++) {
            if (grey(i + "")) {
                count++;
            }
        }
        System.out.println(count);
    }
}
