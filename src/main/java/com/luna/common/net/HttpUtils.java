package com.luna.common.net;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Luna
 */
public class HttpUtils {

    private static CloseableHttpClient httpClient;

    private static BasicCookieStore    cookieStore;

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

    /**
     * 请求头构建
     *
     * @param headers
     * @param requestBase
     */
    private static void builderHeader(Map<String, String> headers, HttpRequestBase requestBase) {
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                requestBase.addHeader(e.getKey(), e.getValue());
            }
        }
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
     * get
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
     * put request
     * 
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body
     * @return
     */
    public static HttpResponse doPut(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        try {
            HttpPut request = new HttpPut(buildUrl(host, path, queries));
            builderHeader(headers, request);
            if (StringUtils.isNotBlank(body)) {
                request.setEntity(new StringEntity(body, Charset.defaultCharset()));
            }
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Post form 文件
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, Map<String, String> bodies) {
        HttpPost request = new HttpPost(buildUrl(host, path, queries));
        builderHeader(headers, request);
        if (MapUtils.isNotEmpty(bodies)) {
            List<NameValuePair> nameValuePairList = Lists.newArrayList();
            for (String key : bodies.keySet()) {
                // 传入参数可以为file或者filePath，在此处做转换
                File file = new File(bodies.get(key));
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                // 设置浏览器兼容模式
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                // 设置请求的编码格式
                builder.setCharset(Consts.UTF_8);
                builder.setContentType(ContentType.MULTIPART_FORM_DATA);
                // 添加文件
                builder.addBinaryBody(key, file);
                HttpEntity reqEntity = builder.build();
                request.setEntity(reqEntity);
                nameValuePairList.add(new BasicNameValuePair(key, bodies.get(key)));
            }
        }
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Post String
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body 请求体
     * @return HttpResponse
     * @throws Exception
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
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, byte[] body) {
        HttpPost request = new HttpPost(buildUrl(host, path, queries));
        builderHeader(headers, request);
        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String buildUrlObject(String host, String path, Map<String, Object> queries) {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);

        if (StringUtils.isNotBlank(path)) {
            sbUrl.append(path);
        }

        if (MapUtils.isNotEmpty(queries)) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, Object> query : queries.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && ObjectUtils.isNotEmpty(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (StringUtils.isNotBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (ObjectUtils.isNotEmpty(query.getValue())) {
                        sbQuery.append("=");
                        try {
                            sbQuery.append(URLEncoder.encode(String.valueOf(query.getValue()), CharsetKit.UTF_8));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    /**
     * 构建url
     * 
     * @param host 主机地址
     * @param path 路径
     * @param queries 请求参数
     * @return
     */
    public static String buildUrl(String host, String path, Map<String, String> queries) {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);

        if (StringUtils.isNotBlank(path)) {
            sbUrl.append(path);
        }

        if (MapUtils.isNotEmpty(queries)) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : queries.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && StringUtils.isNotBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (StringUtils.isNotBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (StringUtils.isNotBlank(query.getValue())) {
                        sbQuery.append("=");
                        try {
                            sbQuery.append(URLEncoder.encode(query.getValue(), CharsetKit.UTF_8));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    /**
     * 检测响应体
     *
     * @param httpResponse
     * @return
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
     * @param httpResponse
     * @return
     */
    public static byte[] checkResponseStreamAndGetResult(HttpResponse httpResponse) {
        if (httpResponse == null) {
            throw new RuntimeException();
        }
        if (httpResponse.getStatusLine() == null) {
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
     * @throws UnsupportedEncodingException
     */
    public static String urlEncode(Map<?, ?> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                URLEncoder.encode(entry.getValue().toString(), "UTF-8")));
        }
        return sb.toString();
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
     * @param httpResponse
     * @param statusList
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
     * @param httpResponse
     * @return
     */
    public static String checkResponseAndGetResult(HttpResponse httpResponse) {
        return checkResponseAndGetResult(httpResponse, ImmutableList.of(HttpStatus.SC_OK));
    }



    /**
     * 检查是不是网络路径
     *
     * @param url
     * @return
     */
    public static boolean isNetUrl(String url) {
        boolean reault = false;
        if (url != null) {
            if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("rtsp")
                || url.toLowerCase().startsWith("mms")) {
                reault = true;
            }
        }
        return reault;
    }

}
