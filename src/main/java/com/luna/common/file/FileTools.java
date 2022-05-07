package com.luna.common.file;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.luna.common.constant.Constant;
import com.luna.common.exception.BaseException;
import com.luna.common.file.visitor.MoveVisitor;
import com.luna.common.text.Assert;
import com.luna.common.text.CharsetUtil;
import com.luna.common.text.StringTools;
import org.apache.commons.io.FileUtils;
import static com.luna.common.file.PathUtil.del;
import static java.nio.file.Files.isDirectory;

/**
 * @author Luna
 */
public class FileTools {
    /**
     * 读取文件所有内容
     *
     * @param fileName 文件路径带文件名
     * @return List<String>
     */
    public static List<String> readAllLines(String fileName) {
        Objects.requireNonNull(fileName, "文件路径不能为空");
        try {
            return Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文件或空目录
     *
     * @param file 文件目录 或者 文件名
     */
    public static void deleteIfExists(String file) {
        Objects.requireNonNull(file, "文件路径不能为空");
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
     * @return boolean
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
        Objects.requireNonNull(fileName, "文件路径不能为空");
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
     * @throws IOException 写入异常
     */
    public static void write(String content, String fileName) {
        Objects.requireNonNull(fileName, "文件路径不能为空");
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
        Objects.requireNonNull(fileName, "文件路径不能为空");
        try {
            return Files.readAllBytes(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算文件中行数
     *
     * @param filePath 文件地址
     * @return long 行数
     * @throws RuntimeException
     */
    public static long count(String filePath) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(filePath));
            long skip = reader.skip(Long.MAX_VALUE);
            return reader.getLineNumber();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> read(String filePath, int skip, int row) {
        Objects.requireNonNull(filePath, "文件路径不能为空");
        LineNumberReader reader = null;
        if (skip < 0) {
            skip = 0;
        }
        if (row < 0) {
            row = 1;
        }
        try {
            ArrayList<String> list = Lists.newArrayList();
            reader = new LineNumberReader(new FileReader(filePath));
            reader.setLineNumber(0);
            String s;
            while ((s = reader.readLine()) != null) {
                if (reader.getLineNumber() <= skip) {
                    continue;
                }
                list.add(s);
                for (int i = 0; i < row - 1; i++) {
                    list.add(reader.readLine());
                }
                break;
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert reader != null;
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
     * @param url 网络地址
     * @param file 文件地址
     */
    public static void download(String url, String file) {
        Objects.requireNonNull(file, "文件路径不能为空");
        Objects.requireNonNull(url, "网络路径不能为空");

        try {
            FileUtils.copyURLToFile(new URL(url), new File(file), Constant.FIVE_THOUSAND, Constant.FIVE_THOUSAND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载文件，失败在优先次数内重试
     *
     * @param url 网络路径
     * @param file 保存文件地址
     * @param maxRetry 重试次数
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
     * 写入文件
     * 
     * @param path 文件路径
     * @param content 写入内容
     */
    public static void writeStringToFile(String path, String content) {
        writeStringToFile(path, content, CharsetUtil.defaultCharset(), true);
    }

    /**
     * 路径创建文件夹
     * 
     * @param pathDir 文件夹路径
     */
    public static void createDirectory(String pathDir) {
        try {
            Path path = Paths.get(pathDir);
            if (!isDirectory(path)) {
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
    public static void writeStringToFile(String path, String content, Charset destCharset, boolean override) {
        try {
            Path filePath = Paths.get(path);
            if (Files.notExists(filePath)) {
                if (!override) {
                    throw new FileNotFoundException();
                }
                Files.createFile(filePath);
            }
            FileUtils.writeStringToFile(new File(path), content, destCharset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 写入文件
     *
     * @param file 文件路径
     * @param content 文件内容
     * @param override 不存在是否创建
     */
    public static void writeStringToFile(File file, String content, Charset destCharset, boolean override) {
        String path = file.getAbsolutePath();
        writeStringToFile(path, content, destCharset, override);
    }

    /**
     * 写入文件 不存在抛出异常
     *
     * @param file 文件路径
     * @param content 文件内容
     * @param destCharset 编码格式
     */
    public static void writeStringToFile(File file, String content, Charset destCharset) {
        writeStringToFile(file, content, destCharset, false);
    }

    /**
     * 读取文件
     * 
     * @param path 文件全路径
     */
    public static String readFileToString(String path) {
        return readFileToString(path, Charset.defaultCharset());
    }

    /**
     * 读取文件
     *
     * @param path 文件全路径
     * @param destCharset 文件编码
     */
    public static String readFileToString(String path, Charset destCharset) {
        return readFileToString(new File(path), destCharset);
    }

    /**
     * 读取文件
     *
     * @param file 文件
     * @param destCharset 文件编码
     */
    public static String readFileToString(File file, Charset destCharset) {
        try {
            return FileUtils.readFileToString(file, destCharset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名，不保留扩展名。<br>
     *
     * <pre>
     * FileUtil.rename(file, "aaa.png", true) xx/xx.png =》xx/aaa.png
     * </pre>
     *
     * @param file 被修改的文件
     * @param newName 新的文件名，如需扩展名，需自行在此参数加上，原文件名的扩展名不会被保留
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件
     * @since 5.3.6
     */
    public static File rename(File file, String newName, boolean isOverride) {
        return rename(file, newName, false, isOverride);
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名<br>
     * 重命名有两种模式：<br>
     * 1、isRetainExt为true时，保留原扩展名：
     *
     * <pre>
     * FileUtil.rename(file, "aaa", true) xx/xx.png =》xx/aaa.png
     * </pre>
     *
     * <p>
     * 2、isRetainExt为false时，不保留原扩展名，需要在newName中
     *
     * <pre>
     * FileUtil.rename(file, "aaa.jpg", false) xx/xx.png =》xx/aaa.jpg
     * </pre>
     *
     * @param file 被修改的文件
     * @param newName 新的文件名，包括扩展名
     * @param isRetainExt 是否保留原文件的扩展名，如果保留，则newName不需要加扩展名
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件
     * @see PathUtil#rename(Path, String, boolean)
     * @since 3.0.9
     */
    public static File rename(File file, String newName, boolean isRetainExt, boolean isOverride) {
        if (isRetainExt) {
            final String extName = FileNameUtil.extName(file);
            if (StringTools.isNotBlank(extName)) {
                newName = newName.concat(".").concat(extName);
            }
        }
        return rename(file.toPath(), newName, isOverride).toFile();
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名<br>
     *
     * <pre>
     * FileUtil.rename(file, "aaa.jpg", false) xx/xx.png =》xx/aaa.jpg
     * </pre>
     *
     * @param path 被修改的文件
     * @param newName 新的文件名，包括扩展名
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件Path
     * @since 5.4.1
     */
    public static Path rename(Path path, String newName, boolean isOverride) {
        return move(path, path.resolveSibling(newName), isOverride);
    }

    /**
     * 移动文件或目录<br>
     * 当目标是目录时，会将源文件或文件夹整体移动至目标目录下<br>
     * 例如：move("/usr/aaa", "/usr/bbb")结果为："/usr/bbb/aaa"
     *
     * @param src 源文件或目录路径
     * @param target 目标路径，如果为目录，则移动到此目录下
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件Path
     * @since 5.5.1
     */
    public static Path move(Path src, Path target, boolean isOverride) {
        Assert.notNull(src, "Src path must be not null !");
        Assert.notNull(target, "Target path must be not null !");
        final CopyOption[] options =
            isOverride ? new CopyOption[] {StandardCopyOption.REPLACE_EXISTING} : new CopyOption[] {};
        if (isDirectory(target)) {
            target = target.resolve(src.getFileName());
        }
        // 自动创建目标的父目录
        createDirectory(target.toString());
        try {
            return Files.move(src, target, options);
        } catch (IOException e) {
            // 移动失败，可能是跨分区移动导致的，采用递归移动方式
            try {
                Files.walkFileTree(src, new MoveVisitor(src, target, options));
                // 移动后空目录没有删除，
                del(src);
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
            return target;
        }
    }

    /**
     * 清空目录下所有文件
     * 
     * @param path 目录路径
     */
    public static void cleanDirectory(String path) {
        cleanDirectory(new File(path), false);
    }

    /**
     * 清空目录下所有文件
     *
     * @param path 目录路径
     * @param parent 路径为文件时，是否从父路径清除
     */
    public static void cleanDirectory(String path, boolean parent) {
        cleanDirectory(new File(path), parent);
    }

    /**
     * 清空目录下所有文件
     *
     * @param file 文件系统
     * @param parent 路径为文件时，是否从父路径清除
     */
    public static void cleanDirectory(File file, boolean parent) {
        try {
            if (file.isFile()) {
                if (!parent) {
                    throw new BaseException("文件不是目录");
                }
                file = file.getParentFile();
            }
            FileUtils.cleanDirectory(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
