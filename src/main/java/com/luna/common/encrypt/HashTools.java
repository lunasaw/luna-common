package com.luna.common.encrypt;

import com.luna.common.text.CharsetUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author luna@mac
 * 2021年05月10日 14:11
 */
public class HashTools {

    /**
     * 获取文件或者字符串的MD5值
     *
     * @param data 数据
     * @return
     */
    public static String md5(String data) {
        return EncryptUtils.encryptByCommonIo(data, HashMode.MODE_0.getName());
    }

    public static String md5WithFile(String path, String charset) {
        try {
            return md5(IOUtils.toInputStream(path, charset));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5WithFile(String path) {
        return md5WithFile(path, CharsetUtil.UTF_8);
    }

    public static String md5(InputStream inputStream) {
        return EncryptUtils.encryptByCommonIo(inputStream, HashMode.MODE_1.getName());
    }

    public static String sha256(String data) {
        return EncryptUtils.encryptByCommonIo(data, HashMode.MODE_1410.getName());
    }

    public static String sha256WithFile(String path, String charset) {
        try {
            return sha256(IOUtils.toInputStream(path, charset));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha256WithFile(String path) {
        return sha256WithFile(path, CharsetUtil.UTF_8);
    }

    public static String sha256(InputStream inputStream) {
        return EncryptUtils.encryptByCommonIo(inputStream, HashMode.MODE_1.getName());
    }

    public static boolean checkFileWithSHA256(InputStream path, String sha256) {
        return StringUtils.equals(sha256(path), sha256);
    }

    public static boolean checkFileWithSHA256(String path, String md5) {
        return StringUtils.equals(sha256WithFile(path), md5);
    }

    public static boolean checkFileWithMd5(InputStream path, String md5) {
        return StringUtils.equals(md5(path), md5);
    }

    public static boolean checkFileWithMd5(String path, String md5) {
        return StringUtils.equals(md5WithFile(path), md5);
    }

}
