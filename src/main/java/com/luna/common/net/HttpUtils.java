package com.luna.common.net;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.luna.common.constant.Constant;
import com.luna.common.net.method.HttpDelete;
import com.luna.common.text.CharsetKit;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Luna
 */
public class HttpUtils {

    private static final CloseableHttpClient httpClient;

    private static final BasicCookieStore    cookieStore;

    static {
        SSLConnectionSocketFactory socketFactory = null;
        try {
            // 信任所有
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                (TrustStrategy)(chain, authType) -> true).build();
            socketFactory = new SSLConnectionSocketFactory(sslContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", socketFactory != null ? socketFactory : PlainConnectionSocketFactory.getSocketFactory())
            .build();

        // for proxy debug
        // HttpHost proxy = new HttpHost("localhost", 8888);
        // RequestConfig defaultRequestConfig =
        // RequestConfig.custom().setProxy(proxy).setSocketTimeout(5000).setConnectTimeout(5000)
        // .setConnectionRequestTimeout(5000).build();

        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000)
            .setConnectionRequestTimeout(10000).build();

        cookieStore = new BasicCookieStore();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(200);
        httpClient =
            HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(defaultRequestConfig)
                .setDefaultCookieStore(cookieStore).build();
    }

    public static boolean isUrl(String str) {
        // 转换为小写
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)" // https、http、ftp、rtsp、mms
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return str.matches(regex);
    }

    /**
     * 请求头构建
     *
     * @param headers
     * @param requestBase
     */
    private static void builderHeader(Map<String, String> headers, HttpRequestBase requestBase) {
        if (MapUtils.isEmpty(headers)) {
            return;
        }
        headers.forEach(requestBase::addHeader);
    }

    public static List<Cookie> getCookie() {
        return cookieStore.getCookies();
    }

    public static void addCookie(Cookie cookie) {
        cookieStore.addCookie(cookie);
    }

    public static void addCookie(List<Cookie> cookies) {
        cookies.forEach(cookie -> cookieStore.addCookie(cookie));
    }

    public static void addCookie(Cookie... cookies) {
        Arrays.stream(cookies).forEach(cookie -> cookieStore.addCookie(cookie));
    }

    /**
     * 发送 get 请求
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @return
     * @throws Exception
     */
    public static HttpResponse doGet(String host, String path, Map<String, String> headers,
        Map<String, String> queries) {
        HttpGet request = new HttpGet(buildUrl(host, path, queries));
        builderHeader(headers, request);
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * delete request
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body
     * @return
     */
    public static HttpResponse doDelete(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        try {
            HttpDelete delete = new HttpDelete(buildUrl(host, path, queries));
            builderHeader(headers, delete);
            if (StringUtils.isNotBlank(body)) {
                delete.setEntity(new StringEntity(body, Charset.defaultCharset()));
            }
            return httpClient.execute(delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * PUT 方法请求
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body 请求体
     * @return HttpResponse
     */
    public static HttpResponse doPut(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        try {
            HttpPut httpPut = new HttpPut(buildUrl(host, path, queries));
            builderHeader(headers, httpPut);
            if (StringUtils.isNotBlank(body)) {
                httpPut.setEntity(new StringEntity(body, Charset.defaultCharset()));
            }
            return httpClient.execute(httpPut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * POST 发送文件请求
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param bodies 文件列表
     * @return HttpResponse
     * @throws Exception 运行时异常
     */
    public static HttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, Map<String, String> bodies) {
        HttpPost request = new HttpPost(buildUrl(host, path, queries));
        builderHeader(headers, request);
        if (MapUtils.isNotEmpty(bodies)) {
            bodies.forEach((k, v) -> {
                // 传入参数可以为file或者filePath，在此处做转换
                File file = new File(v);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                // 设置浏览器兼容模式
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                // 设置请求的编码格式
                builder.setCharset(Consts.UTF_8);
                builder.setContentType(ContentType.MULTIPART_FORM_DATA);
                // 添加文件
                builder.addBinaryBody(k, file);
                HttpEntity reqEntity = builder.build();
                request.setEntity(reqEntity);
            });
        }
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * POST请求体为字符串
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body 请求体
     * @return HttpResponse
     * @throws RuntimeException
     */
    public static HttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        HttpPost request = new HttpPost(buildUrl(host, path, queries));
        builderHeader(headers, request);
        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, Charset.defaultCharset()));
        }
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Post stream
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body 请求体
     * @return HttpResponse
     * @throws RuntimeException
     */
    public static HttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, byte[] body) {
        HttpPost request = new HttpPost(buildUrl(host, path, queries));
        builderHeader(headers, request);
        if (ObjectUtils.isNotEmpty(body)) {
            request.setEntity(new ByteArrayEntity(body));
        }
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建请求路径
     *
     * @param host 主机地址
     * @param path 请求路径
     * @return String
     */
    public static String buildUrlHead(String host, String path) {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);
        if (StringUtils.isNotBlank(path)) {
            if (!path.startsWith(Constant.SPRIT)) {
                path += Constant.SPRIT;
            }
            sbUrl.append(path);
        }
        return sbUrl.toString();
    }

    /**
     * 构建请求路径
     *
     * @param host 主机地址
     * @param path 请求路径
     * @param queries 请求参数
     * @return String
     */
    public static String buildUrlObject(String host, String path, Map<String, Object> queries) {

        return buildUrl(host, path, queries);
    }

    /**
     * 构建请求路径
     *
     * @param host 主机地址
     * @param path 请求路径
     * @param queries 请求参数
     * @return String
     */
    public static String buildUrlString(String host, String path, Map<String, String> queries) {

        return buildUrl(host, path, queries);
    }

    /**
     * 构建url
     *
     * @param host 主机地址
     * @param path 路径
     * @param queries 请求参数
     * @return String
     */
    public static String buildUrl(String host, String path, Map<?, ?> queries) {
        StringBuilder sbUrl = new StringBuilder(buildUrlHead(host, path));
        if (MapUtils.isEmpty(queries)) {
            return sbUrl.toString();
        }
        String sbQuery = urlEncode(queries);
        if (StringUtils.isNotBlank(sbQuery)) {
            sbUrl.append(Constant.QUESTION).append(sbQuery);
        }
        return sbUrl.toString();
    }

    /**
     * 检测响应体
     *
     * @param httpResponse 响应体
     * @return String
     */
    public static String checkResponseAndGetResult(HttpResponse httpResponse, boolean isEnsure) {
        if (httpResponse == null) {
            throw new RuntimeException();
        }
        if (httpResponse.getStatusLine() == null) {
            throw new RuntimeException();
        }
        if (isEnsure && HttpStatus.SC_OK != httpResponse.getStatusLine().getStatusCode()) {
            throw new RuntimeException();
        }
        HttpEntity entity = httpResponse.getEntity();
        try {
            return EntityUtils.toString(entity, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测响应体获取相应流
     *
     * @param httpResponse 响应体
     * @return
     */
    public static byte[] checkResponseStreamAndGetResult(HttpResponse httpResponse) {
        if (Objects.isNull(httpResponse)) {
            throw new NullPointerException();
        }
        if (Objects.isNull(httpResponse.getStatusLine())) {
            throw new RuntimeException();
        }
        if (HttpStatus.SC_OK != httpResponse.getStatusLine().getStatusCode()) {
            throw new RuntimeException();
        }
        HttpEntity entity = httpResponse.getEntity();
        try {
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析生成URL
     *
     * @param map 键值对
     * @return 生成的URL尾部
     */
    public static String urlEncode(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((k, v) -> {
            try {
                if (ObjectUtils.isNotEmpty(k) && ObjectUtils.isNotEmpty(v)) {
                    sb.append(String.format("%s=%s",
                        URLEncoder.encode(k.toString(), CharsetKit.UTF_8),
                        URLEncoder.encode(v.toString(), CharsetKit.UTF_8)));
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });

        return sb.toString();
    }

    public static String urlEncodeWithUtf8(String s) {
        return urlEncode(s, CharsetKit.UTF_8);
    }

    public static String urlEncode(String s, String chareset) {
        try {
            return URLEncoder.encode(s, chareset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测响应体并解析
     *
     * @param httpResponse 响应体
     * @param statusList 状态码列表
     * @return 解析字节
     */
    public static byte[] checkResponseStreamAndGetResult(HttpResponse httpResponse, List<Integer> statusList) {
        checkCode(httpResponse, statusList);
        HttpEntity entity = httpResponse.getEntity();
        try {
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测响应体并解析
     *
     * @param httpResponse 响应体
     * @param statusList 状态码列表
     * @return 解析字符串
     */
    public static String checkResponseAndGetResult(HttpResponse httpResponse, List<Integer> statusList) {
        checkCode(httpResponse, statusList);

        HttpEntity entity = httpResponse.getEntity();
        try {
            return EntityUtils.toString(entity, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测状态码
     *
     * @param httpResponse 响应体
     * @param statusList 检测状态表
     */
    private static void checkCode(HttpResponse httpResponse, List<Integer> statusList) {
        if (httpResponse == null) {
            throw new RuntimeException();
        }
        if (httpResponse.getStatusLine() == null) {
            throw new RuntimeException();
        }
        if (!statusList.contains(httpResponse.getStatusLine().getStatusCode())) {
            throw new RuntimeException();
        }
    }

    /**
     * 解析响应体
     *
     * @param httpResponse 响应体
     * @return String
     */
    public static String checkResponseAndGetResult(HttpResponse httpResponse) {
        return checkResponseAndGetResult(httpResponse, ImmutableList.of(HttpStatus.SC_OK));
    }
}
