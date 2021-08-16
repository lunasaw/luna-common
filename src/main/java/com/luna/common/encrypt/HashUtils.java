package com.luna.common.encrypt;

import com.luna.common.text.CharsetKit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * Md5加密方法
 *
 * @author luna
 */
public class HashUtils {

    /**
     * 获取文件或者字符串的MD5值
     *
     * @param data
     * @return
     */
    public static String md5(String data) {
        return EncryptUtils.dataEncryptByJdk(data,  HashMode.MODE_0.getName());
    }

    public static String md5WithFile(String path) {
        try {
            return md5(IOUtils.toInputStream(path, CharsetKit.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5(InputStream inputStream) {
        return EncryptUtils.streamEncryptByJdk(inputStream,  HashMode.MODE_0.getName());
    }

    public static String sha256(String data) {
        return EncryptUtils.dataEncryptByJdk(data,  HashMode.MODE_1.getName());
    }

    public static String sha256WithFile(String path) {
        try {
            return sha256(IOUtils.toInputStream(path, CharsetKit.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha256(InputStream inputStream) {
        return EncryptUtils.streamEncryptByJdk(inputStream, HashMode.MODE_1.getName());
    }

    /**
     * 检查输入流hash
     * @param path 文件路径
     * @param sha256 hash值
     * @return
     */
    public static boolean checkFileWithSHA256(InputStream path, String sha256) {
        return StringUtils.equals(sha256(path), sha256);
    }

    /**
     * 检查文件hash
     * @param path 文件路径
     * @param md5 md5值
     * @return
     */
    public static boolean checkFileWithSHA256(String path, String md5) {
        return StringUtils.equals(sha256WithFile(path), md5);
    }

    /**
     * 检查输入流hash
     * @param path 文件路径
     * @param md5 md5值
     * @return
     */
    public static boolean checkFileWithMd5(InputStream path, String md5) {
        return StringUtils.equals(md5(path), md5);
    }

    /**
     * 检查输入流hash
     * @param path 文件路径
     * @param md5 md5值
     * @return
     */
    public static boolean checkFileWithMd5(String path, String md5) {
        return StringUtils.equals(md5WithFile(path), md5);
    }
}
