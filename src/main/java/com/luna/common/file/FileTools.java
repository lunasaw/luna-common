package com.luna.common.file;

import static com.luna.common.file.PathUtil.del;
import static com.luna.common.os.OSinfo.isWindows;
import static java.nio.file.Files.isDirectory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.luna.common.check.Assert;
import com.luna.common.constant.CharPoolConstant;
import com.luna.common.constant.Constant;
import com.luna.common.exception.BaseException;
import com.luna.common.file.visitor.MoveVisitor;
import com.luna.common.io.IoUtil;
import com.luna.common.text.CharsetUtil;
import com.luna.common.text.StringTools;

/**
 * @author Luna
 */
public class FileTools {

    /**
     * 获取临时文件路径（绝对路径）
     *
     * @return 临时文件路径
     * @since 4.0.6
     */
    public static String getTmpDirPath() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 获取临时文件目录
     *
     * @return 临时文件目录
     * @since 4.0.6
     */
    public static File getTmpDir() {
        return new File(getTmpDirPath());
    }

    /**
     * 获取用户路径（绝对路径）
     *
     * @return 用户路径
     * @since 4.0.6
     */
    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    /**
     * 获取用户目录
     *
     * @return 用户目录
     * @since 4.0.6
     */
    public static File getUserHomeDir() {
        return new File(getUserHomePath());
    }

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
     * 判断一个文件是否存在
     *
     * @param fileName 文件路径
     * @return boolean
     */
    public static boolean notExists(String fileName) {
        return !isExists(fileName);
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
     * 将流的内容写入文件
     *
     * @param in 输入流，不关闭
     * @param isCloseIn 是否关闭输入流
     * @return dest
     * @throws IOException IO异常
     * @since 5.5.2
     */
    public static File write(InputStream in, File file, boolean isCloseIn) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            IoUtil.copy(in, out);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            IoUtil.close(out);
            if (isCloseIn) {
                IoUtil.close(in);
            }
        }
        return file;
    }

    /**
     * 将流的内容写入文件
     *
     * @param in 输入流，不关闭
     * @param isCloseIn 是否关闭输入流
     * @return dest
     * @throws IOException IO异常
     * @since 5.5.2
     */
    public static File write(InputStream in, String file, boolean isCloseIn) throws IOException {
        return write(in, new File(file), isCloseIn);
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
            if (StringUtils.isNotBlank(extName)) {
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

    /**
     * 获取{@link Path}文件名
     *
     * @param path {@link Path}
     * @return 文件名
     * @since 5.7.15
     */
    public static String getName(Path path) {
        if (null == path) {
            return null;
        }
        return path.getFileName().toString();
    }

    /**
     * 获得一个输出流对象
     *
     * @param file 文件
     * @return 输出流对象
     * @throws IOException IO异常
     */
    public static BufferedOutputStream getOutputStream(File file) {
        final OutputStream out;
        try {
            out = Files.newOutputStream(touch(file).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return IoUtil.toBuffered(out);
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param path 相对ClassPath的目录或者绝对路径目录，使用POSIX风格
     * @return 文件，若路径为null，返回null
     * @throws RuntimeException IO异常
     */
    public static File touch(String path) throws RuntimeException {
        if (path == null) {
            return null;
        }
        return touch(file(path));
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     * @throws IOException IO异常
     */
    public static File touch(File file) {
        if (null == file) {
            return null;
        }
        if (!file.exists()) {
            try {
                Files.createFile(file.toPath());
                // noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    /**
     * 创建File对象，自动识别相对或绝对路径，相对路径将自动从ClassPath下寻找
     *
     * @param path 相对ClassPath的目录或者绝对路径目录
     * @return File
     */
    public static File file(String path) {
        if (null == path) {
            return null;
        }
        return new File(path);
    }

    /**
     * 创建File对象<br>
     * 根据的路径构建文件，在Win下直接构建，在Linux下拆分路径单独构建
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parent 父文件对象
     * @param path 文件路径
     * @return File
     */
    public static File file(File parent, String path) {
        if (StringTools.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return checkSlip(parent, buildFile(parent, path));
    }

    /**
     * 获得输入流
     *
     * @param file 文件
     * @return 输入流
     * @throws IOException 文件未找到
     */
    public static BufferedInputStream getInputStream(File file) {
        return IoUtil.toBuffered(IoUtil.toStream(file));
    }

    /**
     * 获得一个带缓存的写入对象
     *
     * @param isAppend 是否追加
     * @return BufferedReader对象
     * @throws IOException IO异常
     */
    public static BufferedWriter getWriter(File file, Charset charset, boolean isAppend) {
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileTools.touch(file), isAppend), charset));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得一个文件读取器
     *
     * @param file 文件
     * @param charset 字符集
     * @return BufferedReader对象
     * @throws RuntimeException IO异常
     */
    public static BufferedReader getReader(File file, Charset charset) {
        return IoUtil.getReader(getInputStream(file), charset);
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指Path对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @throws RuntimeException IO异常
     * @see Files#isSameFile(Path, Path)
     * @since 5.4.1
     */
    public static boolean equals(Path file1, Path file2) throws RuntimeException {
        try {
            return Files.isSameFile(file1, file2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指File对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @throws RuntimeException IO异常
     */
    public static boolean equals(File file1, File file2) throws RuntimeException {
        Assert.notNull(file1);
        Assert.notNull(file2);
        if (false == file1.exists() || false == file2.exists()) {
            // 两个文件都不存在判断其路径是否相同， 对于一个存在一个不存在的情况，一定不相同
            return false == file1.exists()//
                && false == file2.exists()//
                && pathEquals(file1, file2);
        }
        return equals(file1.toPath(), file2.toPath());
    }

    /**
     * 文件路径是否相同<br>
     * 取两个文件的绝对路径比较，在Windows下忽略大小写，在Linux下不忽略。
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 文件路径是否相同
     * @since 3.0.9
     */
    public static boolean pathEquals(File file1, File file2) {
        if (isWindows()) {
            // Windows环境
            try {
                if (StringTools.equalsIgnoreCase(file1.getCanonicalPath(), file2.getCanonicalPath())) {
                    return true;
                }
            } catch (Exception e) {
                if (StringTools.equalsIgnoreCase(file1.getAbsolutePath(), file2.getAbsolutePath())) {
                    return true;
                }
            }
        } else {
            // 类Unix环境
            try {
                if (StringTools.equals(file1.getCanonicalPath(), file2.getCanonicalPath())) {
                    return true;
                }
            } catch (Exception e) {
                if (StringTools.equals(file1.getAbsolutePath(), file2.getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查父完整路径是否为自路径的前半部分，如果不是说明不是子路径，可能存在slip注入。
     * <p>
     * 见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parentFile 父文件或目录
     * @param file 子文件或目录
     * @return 子文件或目录
     * @throws IllegalArgumentException 检查创建的子文件不在父目录中抛出此异常
     */
    public static File checkSlip(File parentFile, File file) throws IllegalArgumentException {
        if (null != parentFile && null != file) {
            if (!isSub(parentFile, file)) {
                throw new IllegalArgumentException("New file is outside of the parent dir: " + file.getName());
            }
        }
        return file;
    }

    /**
     * 判断给定的目录是否为给定文件或文件夹的子目录
     *
     * @param parent 父目录
     * @param sub 子目录
     * @return 子目录是否为父目录的子目录
     * @since 4.5.4
     */
    public static boolean isSub(File parent, File sub) {
        Assert.notNull(parent);
        Assert.notNull(sub);
        return isSub(parent.toPath(), sub.toPath());
    }

    /**
     * 判断给定的目录是否为给定文件或文件夹的子目录
     *
     * @param parent 父目录
     * @param sub 子目录
     * @return 子目录是否为父目录的子目录
     * @since 5.5.5
     */
    public static boolean isSub(Path parent, Path sub) {
        return PathUtil.toAbsNormal(sub).startsWith(PathUtil.toAbsNormal(parent));
    }

    /**
     * 根据压缩包中的路径构建目录结构，在Win下直接构建，在Linux下拆分路径单独构建
     *
     * @param outFile 最外部路径
     * @param fileName 文件名，可以包含路径
     * @return 文件或目录
     * @since 5.0.5
     */
    private static File buildFile(File outFile, String fileName) {
        // 替换Windows路径分隔符为Linux路径分隔符，便于统一处理
        fileName = fileName.replace('\\', '/');
        if (false == isWindows()
            // 检查文件名中是否包含"/"，不考虑以"/"结尾的情况
            && fileName.lastIndexOf(CharPoolConstant.SLASH, fileName.length() - 2) > 0) {
            // 在Linux下多层目录创建存在问题，/会被当成文件名的一部分，此处做处理
            // 使用/拆分路径（zip中无\），级联创建父目录
            final List<String> pathParts = Splitter.on('/').splitToList(fileName);
            final int lastPartIndex = pathParts.size() - 1;// 目录个数
            for (int i = 0; i < lastPartIndex; i++) {
                // 由于路径拆分，slip不检查，在最后一步检查
                outFile = new File(outFile, pathParts.get(i));
            }
            // noinspection ResultOfMethodCallIgnored
            outFile.mkdirs();
            // 最后一个部分如果非空，作为文件名
            fileName = pathParts.get(lastPartIndex);
        }
        return new File(outFile, fileName);
    }

    /**
     * 安全地级联创建目录 (确保并发环境下能创建成功)
     *
     * <pre>
     *     并发环境下，假设 test 目录不存在，如果线程A mkdirs "test/A" 目录，线程B mkdirs "test/B"目录，
     *     其中一个线程可能会失败，进而导致以下代码抛出 FileNotFoundException 异常
     *
     *     file.getParentFile().mkdirs(); // 父目录正在被另一个线程创建中，返回 false
     *     file.createNewFile(); // 抛出 IO 异常，因为该线程无法感知到父目录已被创建
     * </pre>
     *
     * @param dir 待创建的目录
     * @param tryCount 最大尝试次数
     * @param sleepMillis 线程等待的毫秒数
     * @return true表示创建成功，false表示创建失败
     * @author z8g
     * @since 5.7.21
     */
    public static boolean mkdirsSafely(File dir, int tryCount, long sleepMillis) {
        if (dir == null) {
            return false;
        }
        if (dir.isDirectory()) {
            return true;
        }
        for (int i = 1; i <= tryCount; i++) { // 高并发场景下，可以看到 i 处于 1 ~ 3 之间
            // 如果文件已存在，也会返回 false，所以该值不能作为是否能创建的依据，因此不对其进行处理
            // noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            if (dir.exists()) {
                return true;
            }
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return dir.exists();
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        if (null == file) {
            return null;
        }
        return mkdir(getParent(file, 1));
    }

    /**
     * 获取指定层级的父路径
     *
     * <pre>
     * getParent(file("d:/aaa/bbb/cc/ddd", 0)) -》 "d:/aaa/bbb/cc/ddd"
     * getParent(file("d:/aaa/bbb/cc/ddd", 2)) -》 "d:/aaa/bbb"
     * getParent(file("d:/aaa/bbb/cc/ddd", 4)) -》 "d:/"
     * getParent(file("d:/aaa/bbb/cc/ddd", 5)) -》 null
     * </pre>
     *
     * @param file 目录或文件
     * @param level 层级
     * @return 路径File，如果不存在返回null
     * @since 4.1.2
     */
    public static File getParent(File file, int level) {
        if (level < 1 || null == file) {
            return file;
        }

        File parentFile;
        try {
            parentFile = file.getCanonicalFile().getParentFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (1 == level) {
            return parentFile;
        }
        return getParent(parentFile, level - 1);
    }

    /**
     * 创建文件夹，会递归自动创建其不存在的父文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型<br>
     *
     * @param dir 目录
     * @return 创建的目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (false == dir.exists()) {
            mkdirsSafely(dir, 5, 1);
        }
        return dir;
    }

    public static void copy(File src, File dest) {
        try {
            copy(src, dest, false, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copy(File src, File dest, Boolean isOverride, Boolean isCopyAttributes) throws IOException {
        // check
        Assert.notNull(src, "Source File is null !");
        if (!src.exists()) {
            throw new RuntimeException("File not exist: " + src);
        }
        Assert.notNull(dest, "Destination File or directiory is null !");
        if (FileTools.equals(src, dest)) {
            return;
        }
        final ArrayList<CopyOption> optionList = new ArrayList<>(2);
        if (isOverride) {
            optionList.add(StandardCopyOption.REPLACE_EXISTING);
        }
        if (isCopyAttributes) {
            optionList.add(StandardCopyOption.COPY_ATTRIBUTES);
        }

        if (src.isDirectory()) {
            throw new RuntimeException();
        }
        Files.copy(src.toPath(), dest.toPath(), optionList.toArray(new CopyOption[0]));
    }
}
