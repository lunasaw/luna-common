package com.luna.common.encrypt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author luna@mac
 * 2021年05月10日 14:27
 */
public class EncryptUtils {

    /** 首先初始化一个字符数组，用来存放每个16进制字符 */
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
        'e', 'f'};

    /**
     * 根据内容生成一串校验和
     *
     * @param md5Prefix 指定前缀
     * @param md5Postfix 指定后缀
     * @param toDigest 原内容
     * @return 校验和
     * @throws Exception
     */
    public static String md5Checksum(String md5Prefix, String md5Postfix, String toDigest) {

        try {
            String md5Unid = md5Prefix + toDigest + md5Postfix;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] checksum = md5.digest(md5Unid.getBytes());

            return Integer.toHexString(checksum[0] & 0xff) +
                Integer.toHexString(checksum[1] & 0xff);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得一个字符串的encrypt值
     *
     * @param input 输入的字符串
     * @return 输入字符串的MD5值
     *
     */
    public static String dataEncryptByJdk(String input, String hashType) {
        try {
            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest = MessageDigest.getInstance(hashType);
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = input.getBytes(StandardCharsets.UTF_8);
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JDK 获取文件唯一值
     *
     * @param path 文件路径
     * @param hashType 密文类型
     * @return
     */
    public static String fileEncryptByJdk(String path, String hashType) {
        try {
            if (Files.exists(Paths.get(path))) {
                FileInputStream in = new FileInputStream(path);
                return streamEncryptByJdk(in, hashType);
            }
            return null;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JDK Md5输入流
     *
     * @param in
     * @param hashType
     * @return
     */
    public static String streamEncryptByJdk(InputStream in, String hashType) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance(hashType);
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                messagedigest.update(buffer, 0, read);
            }
            in.close();
            return byteArrayToHex(messagedigest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符数组组合成字符串返回
     *
     * @param byteArray
     * @return
     */
    public static String byteArrayToHex(byte[] byteArray) {
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        return new String(resultCharArray);
    }

    /**
     * Commons.io 获取md5值
     *
     * @param in
     * @return
     */
    public static String encryptByCommonIo(InputStream in, String hashType) {
        try {
            switch (hashType) {
                case "sha256":
                    return DigestUtils.sha256Hex(in);
                case "md5":
                    return DigestUtils.md5Hex(in);
                case "md2":
                    return DigestUtils.md2Hex(in);
                case "sha512":
                    return DigestUtils.sha512Hex(in);
                case "sha384":
                    return DigestUtils.sha384Hex(in);
                default:
                    return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Commons.io 获取md5值
     *
     * @param data
     * @return
     */
    public static String encryptByCommonIo(String data, String hashType) {
        switch (hashType) {
            case "SHA-256":
                return DigestUtils.sha256Hex(data);
            case "MD5":
                return DigestUtils.md5Hex(data);
            case "MD2":
                return DigestUtils.md2Hex(data);
            case "SHA-512":
                return DigestUtils.sha512Hex(data);
            case "SHA-384":
                return DigestUtils.sha384Hex(data);
            default:
                return null;
        }
    }
}
