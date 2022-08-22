package com.luna.common.file;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.luna.common.constant.CharPoolConstant;
import com.luna.common.constant.StrPoolConstant;
import com.luna.common.regex.ReUtil;
import com.luna.common.text.CharsetUtil;
import com.luna.common.text.StringTools;
import com.luna.common.utils.Assert;
import oshi.util.FileUtil;
import sun.net.util.URLUtil;

/**
 * 文件名相关工具类
 *
 * @author looly
 * @since 5.4.1
 */
public class FileNameUtil {


    /**
     * 针对ClassPath路径的伪协议前缀（兼容Spring）: "classpath:"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * URL 前缀表示文件: "file:"
     */
    public static final String FILE_URL_PREFIX = "file:";
    /**
     * URL 前缀表示jar: "jar:"
     */
    public static final String JAR_URL_PREFIX = "jar:";
    /**
     * URL 前缀表示war: "war:"
     */
    public static final String WAR_URL_PREFIX = "war:";
    /**
     * URL 协议表示文件: "file"
     */
    public static final String URL_PROTOCOL_FILE = "file";
    /**
     * URL 协议表示Jar文件: "jar"
     */
    public static final String URL_PROTOCOL_JAR = "jar";
    /**
     * URL 协议表示zip文件: "zip"
     */
    public static final String URL_PROTOCOL_ZIP = "zip";
    /**
     * URL 协议表示WebSphere文件: "wsjar"
     */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";
    /**
     * URL 协议表示JBoss zip文件: "vfszip"
     */
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";
    /**
     * URL 协议表示JBoss文件: "vfsfile"
     */
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    /**
     * URL 协议表示JBoss VFS资源: "vfs"
     */
    public static final String URL_PROTOCOL_VFS = "vfs";
    /**
     * Jar路径以及内部文件路径的分界符: "!/"
     */
    public static final String JAR_URL_SEPARATOR = "!/";
    /**
     * WAR路径及内部文件路径分界符
     */
    public static final String WAR_URL_SEPARATOR = "*/";

    /**
     * .java文件扩展名
     */
    public static final String   EXT_JAVA                      = ".java";
    /**
     * .class文件扩展名
     */
    public static final String   EXT_CLASS                     = ".class";
    /**
     * .jar文件扩展名
     */
    public static final String   EXT_JAR                       = ".jar";

    /**
     * 类Unix路径分隔符
     */
    public static final char     UNIX_SEPARATOR                = StrPoolConstant.C_SLASH;
    /**
     * Windows路径分隔符
     */
    public static final char     WINDOWS_SEPARATOR             = StrPoolConstant.C_BACKSLASH;

    /**
     * Windows下文件名中的无效字符
     */
    private static final Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|]");

    // -------------------------------------------------------------------------------------------- name start

    /**
     * 返回文件名
     *
     * @param file 文件
     * @return 文件名
     * @since 4.1.13
     */
    public static String getName(File file) {
        return (null != file) ? file.getName() : null;
    }

    /**
     * 返回文件名<br>
     * 
     * <pre>
     * "d:/test/aaa" 返回 "aaa"
     * "/test/aaa.jpg" 返回 "aaa.jpg"
     * </pre>
     *
     * @param filePath 文件
     * @return 文件名
     * @since 4.1.13
     */
    public static String getName(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }
        if (CharsetUtil.isFileSeparator(filePath.charAt(len - 1))) {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (CharsetUtil.isFileSeparator(c)) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 获取文件后缀名，扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     * @see #extName(File)
     * @since 5.3.8
     */
    public static String getSuffix(File file) {
        return extName(file);
    }

    /**
     * 获得文件后缀名，扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     * @see #extName(String)
     * @since 5.3.8
     */
    public static String getSuffix(String fileName) {
        return extName(fileName);
    }

    /**
     * 返回主文件名
     *
     * @param file 文件
     * @return 主文件名
     * @see #mainName(File)
     * @since 5.3.8
     */
    public static String getPrefix(File file) {
        return mainName(file);
    }

    /**
     * 返回主文件名
     *
     * @param fileName 完整文件名
     * @return 主文件名
     * @see #mainName(String)
     * @since 5.3.8
     */
    public static String getPrefix(String fileName) {
        return mainName(fileName);
    }

    /**
     * 返回主文件名
     *
     * @param file 文件
     * @return 主文件名
     */
    public static String mainName(File file) {
        if (file.isDirectory()) {
            return file.getName();
        }
        return mainName(file.getName());
    }

    /**
     * 返回主文件名
     *
     * @param fileName 完整文件名
     * @return 主文件名
     */
    public static String mainName(String fileName) {
        if (null == fileName) {
            return null;
        }
        int len = fileName.length();
        if (0 == len) {
            return fileName;
        }
        if (CharsetUtil.isFileSeparator(fileName.charAt(len - 1))) {
            len--;
        }

        int begin = 0;
        int end = len;
        char c;
        for (int i = len - 1; i >= 0; i--) {
            c = fileName.charAt(i);
            if (len == end && StrPoolConstant.C_DOT == c) {
                // 查找最后一个文件名和扩展名的分隔符：.
                end = i;
            }
            // 查找最后一个路径分隔符（/或者\），如果这个分隔符在.之后，则继续查找，否则结束
            if (CharsetUtil.isFileSeparator(c)) {
                begin = i + 1;
                break;
            }
        }

        return fileName.substring(begin, end);
    }

    /**
     * 获取文件扩展名（后缀名），扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     */
    public static String extName(File file) {
        if (null == file) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }
        return extName(file.getName());
    }

    /**
     * 获得文件的扩展名（后缀名），扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String extName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf(StrPoolConstant.DOT);
        if (index == -1) {
            return StrPoolConstant.EMPTY;
        } else {
            String ext = fileName.substring(index + 1);
            // 扩展名中不能包含路径相关的符号
            return StringTools.containsAny(ext, UNIX_SEPARATOR, WINDOWS_SEPARATOR) ? StrPoolConstant.EMPTY : ext;
        }
    }

    /**
     * 清除文件名中的在Windows下不支持的非法字符，包括： \ / : * ? " &lt; &gt; |
     *
     * @param fileName 文件名（必须不包括路径，否则路径符将被替换）
     * @return 清理后的文件名
     * @since 3.3.1
     */
    public static String cleanInvalid(String fileName) {
        return StringTools.isBlank(fileName) ? fileName : ReUtil.delAll(FILE_NAME_INVALID_PATTERN_WIN, fileName);
    }

    /**
     * 文件名中是否包含在Windows下不支持的非法字符，包括： \ / : * ? " &lt; &gt; |
     *
     * @param fileName 文件名（必须不包括路径，否则路径符将被替换）
     * @return 是否包含非法字符
     * @since 3.3.1
     */
    public static boolean containsInvalid(String fileName) {
        return (false == StringTools.isBlank(fileName)) && ReUtil.contains(FILE_NAME_INVALID_PATTERN_WIN, fileName);
    }

    /**
     * 根据文件名检查文件类型，忽略大小写
     *
     * @param fileName 文件名，例如hutool.png
     * @param extNames 被检查的扩展名数组，同一文件类型可能有多种扩展名，扩展名不带“.”
     * @return 是否是指定扩展名的类型
     * @since 5.5.2
     */
    public static boolean isType(String fileName, String... extNames) {
        return StringTools.equalsAnyIgnoreCase(extName(fileName), extNames);
    }

    /**
     * 获得相对子路径，忽略大小写
     * <p>
     * 栗子：
     *
     * <pre>
     * dirPath: d:/aaa/bbb    filePath: d:/aaa/bbb/ccc     =》    ccc
     * dirPath: d:/Aaa/bbb    filePath: d:/aaa/bbb/ccc.txt     =》    ccc.txt
     * dirPath: d:/Aaa/bbb    filePath: d:/aaa/bbb/     =》    ""
     * </pre>
     *
     * @param dirPath  父路径
     * @param filePath 文件路径
     * @return 相对子路径
     */
    public static String subPath(String dirPath, String filePath) {
        if (StringTools.isNotEmpty(dirPath) && StringTools.isNotEmpty(filePath)) {

            dirPath = StringTools.removeSuffix(normalize(dirPath), "/");
            filePath = normalize(filePath);

            final String result = StringTools.substringAfter(filePath, dirPath);
            return StringTools.removeStart(result, "/");
        }
        return filePath;
    }

    /**
     * 获得相对子路径
     * <p>
     * 栗子：
     *
     * <pre>
     * dirPath: d:/aaa/bbb    filePath: d:/aaa/bbb/ccc     =》    ccc
     * dirPath: d:/Aaa/bbb    filePath: d:/aaa/bbb/ccc.txt     =》    ccc.txt
     * </pre>
     *
     * @param rootDir 绝对父路径
     * @param file    文件
     * @return 相对子路径
     */
    public static String subPath(String rootDir, File file) {
        try {
            return subPath(rootDir, file.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修复路径<br>
     * 如果原路径尾部有分隔符，则保留为标准分隔符（/），否则不保留
     * <ol>
     * <li>1. 统一用 /</li>
     * <li>2. 多个 / 转换为一个 /</li>
     * <li>3. 去除左边空格</li>
     * <li>4. .. 和 . 转换为绝对路径，当..多于已有路径时，直接返回根路径</li>
     * </ol>
     * <p>
     * 栗子：
     *
     * <pre>
     * "/foo//" =》 "/foo/"
     * "/foo/./" =》 "/foo/"
     * "/foo/../bar" =》 "/bar"
     * "/foo/../bar/" =》 "/bar/"
     * "/foo/../bar/../baz" =》 "/baz"
     * "/../" =》 "/"
     * "foo/bar/.." =》 "foo"
     * "foo/../bar" =》 "bar"
     * "foo/../../bar" =》 "bar"
     * "//server/foo/../bar" =》 "/server/bar"
     * "//server/../bar" =》 "/bar"
     * "C:\\foo\\..\\bar" =》 "C:/bar"
     * "C:\\..\\bar" =》 "C:/bar"
     * "~/foo/../bar/" =》 "~/bar/"
     * "~/../bar" =》 普通用户运行是'bar的home目录'，ROOT用户运行是'/bar'
     * </pre>
     *
     * @param path 原路径
     * @return 修复后的路径
     */
    public static String normalize(String path) {
        if (path == null) {
            return null;
        }

        // 兼容Spring风格的ClassPath路径，去除前缀，不区分大小写
        String pathToUse = StringTools.substringBefore(path, CLASSPATH_URL_PREFIX);
        // 去除file:前缀
        pathToUse = StringTools.substringBefore(pathToUse, FILE_URL_PREFIX);

        // 识别home目录形式，并转换为绝对路径
        if (StringTools.startWith(pathToUse, '~')) {
            pathToUse = FileTools.getUserHomePath() + pathToUse.substring(1);
        }

        // 统一使用斜杠
        pathToUse = pathToUse.replaceAll("[/\\\\]+", StrPoolConstant.SLASH);
        // 去除开头空白符，末尾空白符合法，不去除
        pathToUse = StringTools.trimLeadingWhitespace(pathToUse);
        //兼容Windows下的共享目录路径（原始路径如果以\\开头，则保留这种路径）
        if (path.startsWith("\\\\")) {
            pathToUse = "\\" + pathToUse;
        }

        String prefix = StrPoolConstant.EMPTY;
        int prefixIndex = pathToUse.indexOf(CharPoolConstant.COLON);
        if (prefixIndex > -1) {
            // 可能Windows风格路径
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (StringTools.startWith(prefix, CharPoolConstant.SLASH)) {
                // 去除类似于/C:这类路径开头的斜杠
                prefix = prefix.substring(1);
            }
            if (false == prefix.contains(StrPoolConstant.SLASH)) {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            } else {
                // 如果前缀中包含/,说明非Windows风格path
                prefix = StringTools.EMPTY;
            }
        }
        if (pathToUse.startsWith(StrPoolConstant.SLASH)) {
            prefix += StrPoolConstant.SLASH;
            pathToUse = pathToUse.substring(1);
        }

        List<String> pathList = Splitter.on(CharPoolConstant.SLASH).splitToList(pathToUse);

        List<String> pathElements = new LinkedList<>();
        int tops = 0;
        String element;
        for (int i = pathList.size() - 1; i >= 0; i--) {
            element = pathList.get(i);
            // 只处理非.的目录，即只处理非当前目录
            if (false == StrPoolConstant.DOT.equals(element)) {
                if (StrPoolConstant.DOUBLE_DOT.equals(element)) {
                    tops++;
                } else {
                    if (tops > 0) {
                        // 有上级目录标记时按照个数依次跳过
                        tops--;
                    } else {
                        // Normal path element found.
                        pathElements.add(0, element);
                    }
                }
            }
        }

        // issue#1703@Github
        if (tops > 0 && StringTools.isEmpty(prefix)) {
            // 只有相对路径补充开头的..，绝对路径直接忽略之
            while (tops-- > 0) {
                //遍历完节点发现还有上级标注（即开头有一个或多个..），补充之
                // Normal path element found.
                pathElements.add(0, StrPoolConstant.DOUBLE_DOT);
            }
        }

        return prefix + Joiner.on(StrPoolConstant.SLASH).join(pathElements);
    }
    // -------------------------------------------------------------------------------------------- name end
}
