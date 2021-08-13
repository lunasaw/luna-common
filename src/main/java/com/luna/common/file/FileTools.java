package com.luna.common.file;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.luna.common.constant.Constant;
import com.luna.common.net.base.HttpBaseUtils;
import com.luna.common.text.CharsetKit;
import org.apache.commons.io.FileUtils;

/**
 * @author Luna
 */
public class FileTools {
    /**
     * 读取文件所有内容
     *
     * @param fileName
     * @return
     */
    public static List<String> readAllLines(String fileName) {
        try {
            return Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
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
    public static boolean isExists(String fileName) {
        return Files.exists(Paths.get(fileName));
    }

    /**
     * 字节写入文件
     * 
     * @param bytes 字节数组
     * @param fileName 文件路径
     */
    public static void write(byte[] bytes, String fileName) {
        try {
            Files.write(Paths.get(fileName), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串写入文件
     * 
     * @param fileName 文件路径
     * @param content 文本内容
     * @throws IOException
     */
    public static void write(String content, String fileName) {
        try {
            Files.write(Paths.get(fileName), content.getBytes(Charset.defaultCharset()));
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
    public static byte[] read(String fileName) {
        try {
            return Files.readAllBytes(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算文件中行数
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static long count(String fileName) {
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(fileName));
            reader.skip(Long.MAX_VALUE);
            return reader.getLineNumber();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    public static void download(String url, String file) {
        try {
            FileUtils.copyURLToFile(new URL(url), new File(file), Constant.FIVE_THOUSAND, Constant.FIVE_THOUSAND);
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
    public static void downloadWithRetry(String url, String file, int maxRetry) {
        for (int i = 0; i < maxRetry - 1; i++) {
            try {
                download(url, file);
                return;
            } catch (Exception e) {
                // do nothing
            }
        }
        download(url, file);
    }

    /**
     * 读取文件
     * 
     * @param path
     * @param content
     */
    public static void writeStringToFile(String path, String content) {
        writeStringToFile(path, content, true);
    }

    /**
     * 路径创建文件夹
     * 
     * @param pathDir
     */
    public static void createDirectory(String pathDir) {
        try {
            Path path = Paths.get(pathDir);
            if (!Files.isDirectory(path)) {
                path = path.getParent();
            }
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 写入文件
     *
     * @param path 文件路径
     * @param content 文件内容
     * @param override 不存在是否创建
     */
    public static void writeStringToFile(String path, String content, boolean override) {
        try {
            Path filePath = Paths.get(path);
            if (Files.notExists(filePath)) {
                if (!override) {
                    return;
                }
                Files.createFile(filePath);
            }
            FileUtils.writeStringToFile(new File(path), content, CharsetKit.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取文件
     * 
     * @param path 文件全路径
     */
    public static String readFileToString(String path) {
        try {
            return FileUtils.readFileToString(new File(path), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 清空目录下所有文件
     * 
     * @param path
     */
    public static void cleanDirectory(String path) {
        try {
            FileUtils.cleanDirectory(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
