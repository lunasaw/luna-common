//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.luna.common.text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ByteUtils {
    public ByteUtils() {}

    public static byte[] subBytes(byte[] bs, int startIndex, int length) {
        byte[] sub = new byte[length];
        System.arraycopy(bs, startIndex, sub, 0, length);
        return sub;
    }

    public static byte[] copy(byte[] bs) {
        return Arrays.copyOf(bs, bs.length);
    }

    public static byte[] concat(byte[] bytes1, byte[] bytes2) {
        byte[] target = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, target, 0, bytes1.length);
        System.arraycopy(bytes2, 0, target, bytes1.length, bytes2.length);
        return target;
    }

    public static List<byte[]> subToSmallBytes(byte[] bs, int minLength, int maxLength) {
        int length = bs.length;
        if (maxLength > length) {
            maxLength = length;
        }

        List<byte[]> list = new ArrayList();

        int randomLen;
        for (int posi = 0; posi < length; posi += randomLen) {
            randomLen = getRandomValue(minLength, maxLength);
            if (posi + randomLen > length) {
                randomLen = length - posi;
            }

            list.add(subBytes(bs, posi, randomLen));
        }

        return list;
    }

    public static List<byte[]> subToSmallBytes(File file, int subLen) throws FileNotFoundException {
        return subToSmallBytes((InputStream)(new FileInputStream(file)), subLen);
    }

    public static List<byte[]> subToSmallBytes(InputStream inputStream, int subLen) {
        List<byte[]> list = new ArrayList();
        // int available = false;
        boolean var4 = false;

        try {
            for (int available = inputStream.available(); available > 0; available = inputStream.available()) {
                byte[] subBytes = new byte[subLen];
                int readLength = inputStream.read(subBytes);
                if (readLength == subLen) {
                    list.add(subBytes);
                } else if (readLength > 0) {
                    list.add(subBytes(subBytes, 0, readLength));
                }
            }
        } catch (IOException var14) {
            System.err.println("Unexpected IOException: " + var14.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (Exception var13) {
            }

        }

        return list;
    }

    private static int getRandomValue(int minLength, int maxLength) {
        Random random = new Random();
        return random.nextInt(maxLength - minLength) + minLength;
    }

    public static byte[] inputStream2ByteArray(String filePath) {
        File file = new File(filePath);
        return inputStream2ByteArray(file);
    }

    public static byte[] inputStream2ByteArray(File file) {
        try {
            InputStream in = new FileInputStream(file);
            byte[] data = toByteArray(in);
            in.close();
            return data;
        } catch (IOException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    private static byte[] toByteArray(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        boolean var3 = false;

        try {
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }

            return out.toByteArray();
        } catch (IOException var5) {
            var5.printStackTrace();
            return null;
        }
    }
}
