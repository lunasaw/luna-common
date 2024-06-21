package com.luna.common.swing;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.luna.common.text.StringTools;

/**
 * 桌面相关工具（平台相关）<br>
 * Desktop 类允许 Java 应用程序启动已在本机桌面上注册的关联应用程序，以处理 URI 或文件。
 *
 * @author looly
 * @since 4.5.7
 */
public class DesktopUtil {

    public static List<GraphicsDevice> getScreenDevices() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        return Arrays.asList(gs);
    }

    /**
     * 获得{@link Desktop}
     *
     * @return {@link Desktop}
     */
    public static Desktop getDsktop() {
        return Desktop.getDesktop();
    }

    /**
     * 使用平台默认浏览器打开指定URL地址
     *
     * @param url URL地址
     */
    public static void browse(String url) {
        try {
            browse(new URI(StringTools.trim(url)));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用平台默认浏览器打开指定URI地址
     *
     * @param uri URI地址
     * @since 4.6.3
     */
    public static void browse(URI uri) {
        final Desktop dsktop = getDsktop();
        try {
            dsktop.browse(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动关联应用程序来打开文件
     *
     * @param file URL地址
     */
    public static void open(File file) {
        final Desktop dsktop = getDsktop();
        try {
            dsktop.open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动关联编辑器应用程序并打开用于编辑的文件
     *
     * @param file 文件
     */
    public static void edit(File file) {
        final Desktop dsktop = getDsktop();
        try {
            dsktop.edit(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用关联应用程序的打印命令, 用本机桌面打印设备来打印文件
     *
     * @param file 文件
     */
    public static void print(File file) {
        final Desktop dsktop = getDsktop();
        try {
            dsktop.print(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用平台默认浏览器打开指定URL地址
     *
     * @param mailAddress 邮件地址
     */
    public static void mail(String mailAddress) {
        final Desktop dsktop = getDsktop();
        try {
            URI uri = new URI(StringTools.trim(mailAddress));
            dsktop.mail(uri);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
