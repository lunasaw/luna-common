package com.luna.common.file;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * @author Luna
 */
public class FileUtils {
    /**
     * 读取文件所有内容
     *
     * @param fileName
     * @return
     */
    public static List<String> readAllLines(String fileName) {
        try {
            return Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文件或空目录
     *
     * @param file
     */
    public static void deleteIfExists(String file) {
        try {
            Files.deleteIfExists(Paths.get(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断一个文件是否存在
     *
     * @param fileName 文件路径
     * @return
     */
    public static boolean isFileExists(String fileName) {
        return Files.exists(Paths.get(fileName));
    }

    /**
     * 字节写入文件
     * 
     * @param bytes 字节数组
     * @param fileName 文件路径
     */
    public static void writeBytesToFile(byte[] bytes, String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                Files.createFile(Paths.get(fileName));
            }
            Files.write(Paths.get(fileName), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件读取字节
     * 
     * @param fileName 文件路径
     * @return 字节数组
     */
    public static byte[] readFileToBytes(String fileName) {
        try {
            return Files.readAllBytes(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** 行数统计时一次性读byte大小 */
    private static final int BYTE_SIZE = 1024 * 8;

    /**
     * 计算文件中行数
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static long countFileLines(String fileName) {
        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(fileName));
            reader.skip(Long.MAX_VALUE);
            long lines = reader.getLineNumber();
            reader.close();
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载文件
     * <p>
     * 若文件已存在，覆盖
     * </p>
     * <p>
     * 有异常时抛出异常
     * </p>
     *
     * @param url
     * @param file
     */
    public static void downloadFile(String url, String file) {
        try {
            org.apache.commons.io.FileUtils.copyURLToFile(new URL(url), new File(file), 5000, 5000);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载文件，失败在优先次数内重试
     *
     * @param url
     * @param file
     * @param maxRetry
     */
    public static void downloadFileWithRetry(String url, String file, int maxRetry) {
        for (int i = 0; i < maxRetry - 1; i++) {
            try {
                downloadFile(url, file);
                return;
            } catch (Exception e) {
                // do nothing
            }
        }
        downloadFile(url, file);
    }

    /**
     * file check
     *
     * @param file
     * @param sha256
     * @return
     */
    public static boolean checkFileWithSHA256(String file, String sha256) {
        try {
            HashCode hash = com.google.common.io.Files.asByteSource(new File(file)).hash(Hashing.sha256());
            String fileHash = hash.toString();
            return StringUtils.equalsIgnoreCase(fileHash, sha256);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * write file
     * 
     * @param fileName
     * @param content
     */
    public static void writeStringToFile(String fileName, String content) {
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                Files.createFile(Paths.get(fileName));
            }
            org.apache.commons.io.FileUtils.writeStringToFile(new File(fileName), content, Charset.forName("utf-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * read file
     * 
     * @param file
     */
    public static String readFileToString(String file) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(new File(file), Charset.forName("utf-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 清空目录下所有文件
     * 
     * @param file
     */
    public static void cleanDirectory(String file) {
        try {
            org.apache.commons.io.FileUtils.cleanDirectory(new File(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
