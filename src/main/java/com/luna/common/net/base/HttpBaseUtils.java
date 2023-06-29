package com.luna.common.net.base;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

import com.luna.common.file.FileTools;
import com.luna.common.io.IoUtil;
import com.luna.common.net.HttpUtils;
import com.luna.common.text.CharsetUtil;
import com.luna.common.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author luna@mac
 * 2021年05月12日 09:42
 */
@Slf4j
public class HttpBaseUtils {

    /**
     * 连接时间超时
     */
    private static final Integer              CONN_TIME_OUT       = 5000;
    /**
     * 读取时间超时
     */
    private static final Integer              READ_TIME_OUT       = 5000;
    private static final int                  DEFAULT_BUFFER_SIZE = 4096;
    private static volatile HttpURLConnection conn                = null;

    public static HttpURLConnection getConn(String url) {
        try {
            URL realUrl = new URL(url);
            if (conn == null) {
                synchronized (HttpURLConnection.class) {
                    if (conn == null) {
                        conn = (HttpURLConnection)realUrl.openConnection();
                    }
                }
            }
            return conn;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * doURL
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body 请求体
     * @return 输入流
     */
    public static InputStream doURL(String host, String path, String method, Map<String, String> headers,
        Map<String, String> queries, String body) {
        try {
            HttpURLConnection conn = getConnection(host, path, method, headers, queries);
            if (!Objects.isNull(body)) {
                conn.setDoOutput(true);
                IoUtil.write(conn.getOutputStream(), CharsetUtil.defaultCharsetName(), true, body);
            }
            return conn.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * doURL
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param file 请求体
     * @return InputStream
     */
    public static InputStream doURL(String host, String path, String method, Map<String, String> headers,
        Map<String, String> queries, byte[] file) {
        try {
            HttpURLConnection conn = getConnection(host, path, method, headers, queries);
            if (!ObjectUtils.isEmpty(file)) {
                conn.setDoOutput(true);
                IOUtils.write(file, conn.getOutputStream());
            }
            return conn.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] doURLWithByte(String host, String path, String method, Map<String, String> headers,
        Map<String, String> queries, File file) {
        return readWithByte(doURL(host, path, method, headers, queries, FileTools.read(file.getAbsolutePath())));
    }

    public static String doURLWithString(String host, String path, String method, Map<String, String> headers,
        Map<String, String> queries, File file) {
        return readWithString(doURL(host, path, method, headers, queries, FileTools.read(file.getAbsolutePath())));
    }

    public static byte[] doURLWithByte(String host, String path, String method, Map<String, String> headers,
        Map<String, String> queries, String body) {
        return readWithByte(doURL(host, path, method, headers, queries, body));
    }

    public static String doURLWithString(String host, String path, String method, Map<String, String> headers,
        Map<String, String> queries, String body) {
        return readWithString(doURL(host, path, method, headers, queries, body));
    }

    /**
     * 流转文件
     *
     * @param url 请求地址
     * @param path 文件路径
     */
    public static void download(String url, String path) {
        try {
            InputStream is = new URL(url).openStream();
            byte[] bytes = readWithByte(is);
            FileTools.write(bytes, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 流读取字节
     *
     * @param inputStream 输入流
     * @param bufferSize 缓冲区大小
     */
    public static byte[] readWithByte(InputStream inputStream, int bufferSize) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final byte[] tmp = new byte[bufferSize];
            int l;
            while ((l = inputStream.read(tmp)) != -1) {
                buffer.write(tmp, 0, l);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取字符
     *
     * @param inputStream 输入流
     * @return String
     */
    public static String readWithString(InputStream inputStream, int bufferSize) {
        try {
            StringBuilder sb = new StringBuilder();
            int cp;
            final char[] tmp = new char[bufferSize];
            while ((cp = inputStream.read()) != -1) {
                sb.append(tmp, 0, cp);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取
     *
     * @param rd 输入字符流
     * @return String
     */
    public static String readWithReader(Reader rd) {
        try {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char)cp);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符编码指定格式读取
     *
     * @param inputStream 输入流
     * @param charsetName 编码格式
     * @return String
     */
    public static String readWithString(InputStream inputStream, String charsetName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charsetName));
            return readWithReader(reader);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取字符
     *
     * @param inputStream 输入流
     * @return String
     */
    public static String readWithString(InputStream inputStream) {
        return readWithString(inputStream, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 读取字节
     *
     * @param inputStream 输入流
     * @return String
     */
    public static byte[] readWithByte(InputStream inputStream) {
        return readWithByte(inputStream, DEFAULT_BUFFER_SIZE);
    }

    private static HttpURLConnection getConnection(String host, String path, String method, Map<String, String> headers,
        Map<String, String> queries) {
        // url参数拼接
        String url = HttpUtils.buildUrl(host, path, queries);
        HttpURLConnection conn = getConn(url);
        conn.setConnectTimeout(CONN_TIME_OUT);
        conn.setReadTimeout(READ_TIME_OUT);
        try {
            conn.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        // request headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        return conn;
    }

    public static CookieStore setCookieManager() {
        CookieManager manager = new CookieManager();
        // 设置cookie策略，只接受与你对话服务器的cookie，而不接收Internet上其它服务器发送的cookie
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(manager);
        return manager.getCookieStore();
    }

    public static void printCookie(CookieStore cookieStore) {
        List<HttpCookie> listCookie = cookieStore.getCookies();
        listCookie.forEach(httpCookie -> {
            System.out.println("--------------------------------------");
            System.out.println("class      : " + httpCookie.getClass());
            System.out.println("comment    : " + httpCookie.getComment());
            System.out.println("commentURL : " + httpCookie.getCommentURL());
            System.out.println("discard    : " + httpCookie.getDiscard());
            System.out.println("domain     : " + httpCookie.getDomain());
            System.out.println("maxAge     : " + httpCookie.getMaxAge());
            System.out.println("name       : " + httpCookie.getName());
            System.out.println("path       : " + httpCookie.getPath());
            System.out.println("portlist   : " + httpCookie.getPortlist());
            System.out.println("secure     : " + httpCookie.getSecure());
            System.out.println("value      : " + httpCookie.getValue());
            System.out.println("version    : " + httpCookie.getVersion());
            System.out.println("httpCookie : " + httpCookie);
        });
    }
}
