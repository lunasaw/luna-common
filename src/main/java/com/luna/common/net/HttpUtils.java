package com.luna.common.net;

import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.auth.DigestScheme;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;

import com.google.common.collect.ImmutableList;
import com.luna.common.constant.StrPoolConstant;
import com.luna.common.file.FileTools;
import com.luna.common.net.method.HttpDelete;
import com.luna.common.text.CharsetUtil;

/**
 * @author Luna
 */
public class HttpUtils {

    public static final HttpClientContext  CLIENT_CONTEXT      = HttpClientContext.create();
    public static final BasicCookieStore   COOKIE_STORE        = new BasicCookieStore();
    private static final HttpClientBuilder HTTP_CLIENT_BUILDER = HttpClients.custom();
    public static int                      MAX_REDIRECTS       = 10;
    /**
     * 最大连接数
     */
    public static int                      MAX_CONN            = 200;
    /**
     * 设置连接建立的超时时间为10s
     */
    public static int                      CONNECT_TIMEOUT     = 10;
    public static int                      RESPONSE_TIMEOUT    = 30;
    public static int                      MAX_ROUTE           = 200;
    public static int                      SOCKET_TIME_OUT     = 100;
    private static CloseableHttpClient     httpClient;

    static {
        init();
    }

    public static void init() {
        SSLConnectionSocketFactory socketFactory = null;
        try {
            // 信任所有
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
            socketFactory = new SSLConnectionSocketFactory(sslContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", socketFactory != null ? socketFactory : PlainConnectionSocketFactory.getSocketFactory())
            .build();

        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setCookieSpec(StandardCookieSpec.STRICT)
            .setMaxRedirects(MAX_REDIRECTS)
            .setResponseTimeout(RESPONSE_TIMEOUT, TimeUnit.SECONDS)
            .setConnectionRequestTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.BASIC, StandardAuthScheme.DIGEST))
            .setProxyPreferredAuthSchemes(Collections.singletonList(StandardAuthScheme.BASIC))
            .build();

        final DnsResolver dnsResolver = new SystemDefaultDnsResolver() {

            @Override
            public InetAddress[] resolve(final String host) throws UnknownHostException {
                if (host.equalsIgnoreCase("localhost")) {
                    return new InetAddress[] {InetAddress.getByAddress(new byte[] {127, 0, 0, 1})};
                } else {
                    return super.resolve(host);
                }
            }

        };

        // 链接管理器
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
            registry, PoolConcurrencyPolicy.STRICT, PoolReusePolicy.LIFO, TimeValue.ofMinutes(5),
            DefaultSchemePortResolver.INSTANCE, dnsResolver, ManagedHttpClientConnectionFactory.INSTANCE);

        // 最大链接数
        cm.setMaxTotal(MAX_CONN);
        cm.setDefaultMaxPerRoute(MAX_ROUTE);
        cm.setDefaultConnectionConfig(ConnectionConfig.custom()
            .setValidateAfterInactivity(TimeValue.ofSeconds(CONNECT_TIMEOUT))
            .setTimeToLive(TimeValue.ofHours(1))
            .build());
        // socket 配置
        cm.setDefaultSocketConfig(SocketConfig.custom()
            .setSoTimeout(SOCKET_TIME_OUT, TimeUnit.SECONDS)
            .build());

        HTTP_CLIENT_BUILDER.setConnectionManager(cm)
            .setDefaultRequestConfig(defaultRequestConfig);

        httpClient = HTTP_CLIENT_BUILDER.build();
    }

    public static void refresh() {
        HTTP_CLIENT_BUILDER.setDefaultCookieStore(COOKIE_STORE);
        httpClient = HTTP_CLIENT_BUILDER.build();
    }

    public static void basicAuth(String userName, String password, String host) {
        authContext(userName, password, host, StandardAuthScheme.BASIC);
    }

    public static void digestAuth(String userName, String password, String host) {
        authContext(userName, password, host, StandardAuthScheme.DIGEST);
    }

    /**
     * 需要用户名basic 验证
     *
     * @param userName
     * @param password
     * @param host
     */
    public static void authContext(String userName, String password, String host, String authType) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password.toCharArray());
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();

        AuthScope authScope = new AuthScope(null, -1);

        AuthCache authCache = CLIENT_CONTEXT.getAuthCache();

        if (StringUtils.isNotEmpty(host)) {
            HttpHost targetHost = new HttpHost(host);

            if (authCache == null) {
                authCache = new BasicAuthCache();
            }

            if (StandardAuthScheme.BASIC.equals(authType)) {
                BasicScheme basicScheme = new BasicScheme();
                authCache.put(targetHost, basicScheme);
                basicScheme.initPreemptive(credentials);
            } else if (StandardAuthScheme.DIGEST.equals(authType)) {
                DigestScheme digestScheme = new DigestScheme();
                authCache.put(targetHost, digestScheme);
            }
        }
        credsProvider.setCredentials(authScope, credentials);

        // Add AuthCache to the execution context
        CLIENT_CONTEXT.setCredentialsProvider(credsProvider);
        CLIENT_CONTEXT.setAuthCache(authCache);
    }

    public static void setProxy(Integer port) {
        setProxy(IPAddressUtil.LOCAL_HOST, port);
    }

    /**
     * 使用代理访问
     *
     * @param host 代理地址
     * @param port 代理端口
     * @return
     */
    public static void setProxy(String host, Integer port) {
        // for proxy debug
        HttpHost proxy = new HttpHost(host, port);
        DefaultProxyRoutePlanner defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(proxy);
        HTTP_CLIENT_BUILDER.setRoutePlanner(defaultProxyRoutePlanner);

        refresh();
    }

    public static void setProxy(String host, Integer port, String username, String password) {
        if (StringUtils.isNotBlank(username)) {
            authContext(username, password, host, StandardAuthScheme.BASIC);
        }
        setProxy(host, port);
    }

    /**
     * 请求头构建
     *
     * @param headers
     * @param requestBase
     */
    public static void builderHeader(Map<String, String> headers, BasicClassicHttpRequest requestBase) {
        if (MapUtils.isEmpty(headers)) {
            return;
        }
        headers.forEach(requestBase::addHeader);
    }

    public static void builderHeader(Map<String, String> headers, AsyncRequestBuilder requestBase) {
        if (MapUtils.isEmpty(headers)) {
            return;
        }
        headers.forEach(requestBase::addHeader);
    }

    public static List<Cookie> getCookie() {
        return COOKIE_STORE.getCookies();
    }

    public static void addCookie(Cookie cookie) {
        COOKIE_STORE.addCookie(cookie);
    }

    public static void addCookie(List<Cookie> cookies) {
        cookies.forEach(COOKIE_STORE::addCookie);
    }

    public static void addCookie(Cookie... cookies) {
        Arrays.stream(cookies).forEach(COOKIE_STORE::addCookie);
    }

    private static <T> T doRequest(HttpClientResponseHandler<T> responseHandler, HttpUriRequestBase request) {
        try {
            if (responseHandler == null) {
                return (T)httpClient.execute(request, CLIENT_CONTEXT);
            }
            return httpClient.execute(request, CLIENT_CONTEXT, responseHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse doHead(String url) {
        HttpHead request = new HttpHead(url);
        CloseableHttpResponse response = doRequest(null, request);
        return response;
    }

    public static HttpResponse doGet(String url, Map<String, String> headers) {
        HttpGet request = new HttpGet(buildUrl(url, StringUtils.EMPTY, new HashMap<>(0)));
        builderHeader(headers, request);
        return doRequest(null, request);
    }

    /**
     * 发送 get 请求
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param responseHandler 响应处理器
     * @return
     * @throws Exception
     */
    public static <T> T doGet(String host, String path, Map<String, String> headers,
        Map<String, String> queries, HttpClientResponseHandler<T> responseHandler) {

        HttpGet request = new HttpGet(buildUrl(host, path, queries));
        builderHeader(headers, request);
        return doRequest(responseHandler, request);
    }

    /**
     * 发送 get 请求
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @return
     */
    public static String doGetHandler(String host, String path, Map<String, String> headers,
        Map<String, String> queries) {
        return doGet(host, path, headers, queries, new BasicHttpClientResponseHandler());
    }

    public static ClassicHttpResponse doGet(String host, String path, Map<String, String> headers, Map<String, String> queries) {
        return doGet(host, path, headers, queries, null);
    }

    /**
     * delete request
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param responseHandler 响应处理器
     * @param body
     * @return
     */
    public static <T> T doDelete(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, HttpClientResponseHandler<T> responseHandler) {
        HttpDelete delete = new HttpDelete(buildUrl(host, path, queries));
        builderHeader(headers, delete);
        if (StringUtils.isNotBlank(body)) {
            delete.setEntity(new StringEntity(body, Charset.defaultCharset()));
        }
        return doRequest(responseHandler, delete);
    }

    public static String doDeleteHandler(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        return doDelete(host, path, headers, queries, body, new BasicHttpClientResponseHandler());
    }

    public static ClassicHttpResponse doDelete(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        return doDelete(host, path, headers, queries, body, null);
    }

    /**
     * PUT 方法请求
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body 请求体
     * @return ClassicHttpResponse
     */
    public static <T> T doPut(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, HttpClientResponseHandler<T> responseHandler) {

        HttpPut httpPut = new HttpPut(buildUrl(host, path, queries));
        builderHeader(headers, httpPut);
        if (StringUtils.isNotBlank(body)) {
            httpPut.setEntity(new StringEntity(body, Charset.defaultCharset()));
        }
        return doRequest(responseHandler, httpPut);
    }

    /**
     * PUT 方法请求
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body 请求体
     * @return ClassicHttpResponse
     */
    public static ClassicHttpResponse doPut(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        return doPut(host, path, headers, queries, body, null);
    }

    public static String doPutHandler(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        return doPut(host, path, headers, queries, body, new BasicHttpClientResponseHandler());
    }

    /**
     * POST 发送文件请求
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param bodies 文件列表
     * @return ClassicHttpResponse
     * @throws Exception 运行时异常
     */
    public static <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, Map<String, String> bodies, HttpClientResponseHandler<T> responseHandler) {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 设置浏览器兼容模式
        builder.setMode(HttpMultipartMode.LEGACY);
        // 设置请求的编码格式
        builder.setCharset(CharsetUtil.defaultCharset());
        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
        if (MapUtils.isNotEmpty(bodies)) {
            bodies.forEach((k, v) -> {
                // 传入参数可以为file或者filePath，在此处做转换
                File file = new File(v);
                // 添加文件
                builder.addBinaryBody(k, file);
            });
        }
        HttpEntity reqEntity = builder.build();
        return doPost(host, path, headers, queries, reqEntity, responseHandler);
    }

    private static void close(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void doPost(String host, String path, Map<String, String> headers,
                               Map<String, String> queries, Map<String, String> bodies, HttpClientResponseHandler<T> responseHandler, FutureCallback<Object> futureCallback) {

        if (MapUtils.isNotEmpty(bodies)) {
            bodies.forEach((k, v) -> {
                boolean notExists = FileTools.notExists(v);
                if (notExists) {
                    return;
                }
                File file = new File(v);
                // 添加文件
                breakingPointUpload(host, path, headers, queries, k, file, futureCallback);
            });
        }

        try {
            responseHandler.handleResponse(new BasicClassicHttpResponse(HttpStatus.SC_OK));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void breakingPointDownload(String host, String path, Map<String, String> headers,
                                                 Map<String, String> queries, FutureCallback<Object> futureCallback) {

    }

    public static <T> void breakingPointUpload(String host, String path, Map<String, String> headers,
                               Map<String, String> queries, String key, File file, FutureCallback<Object> futureCallback) {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 设置浏览器兼容模式
        builder.setMode(HttpMultipartMode.LEGACY);
        // 设置请求的编码格式
        builder.setCharset(CharsetUtil.defaultCharset());
        builder.setContentType(ContentType.MULTIPART_FORM_DATA);

        RandomAccessFile rw = null;
        FileOutputStream fos = null;
        try {
            long length = file.length();
            //每次请求发送文件的大小
            int size = 10485760;
            //根据文件大小计算请求次数
            int round = (int) Math.ceil((double) length / size);
            rw = new RandomAccessFile(file, "rw");
            //文件上传起始位置
            long start = 0L;
            for (int i = 0; i < round; i++) {
                Date startDate = new Date();
                //一次请求的结束位置
                long end = start + size;
                //判断此次请求结束是否大于文件的总大小，如果大于就用文件总大小减去起始位置，得到最后一次请求的结束位置
                if (end > length) {
                    size = (int) (length - start);
                    end = start + size;
                }
                //设置此次请求的数据范围
                headers.put("Range", "bytes=" + start + "-" + end);
                //读取源文件时跳到此次请求的起始位置
                rw.seek(start);
                byte[] bytes = new byte[size];
                //读取源文件，从起始位置到本次请求的结束位置
                int read = rw.read(bytes);
                //创建临时文件，请求时放入请求体上传临时文件
                File tmpFile = new File(System.getProperty("java.io.tmpdir"));
                fos = new FileOutputStream(tmpFile);
                fos.write(bytes, 0, read);
                fos.flush();
                fos.close();
                // 把文件转换成流对象FileBody
                FileBody bin = new FileBody(tmpFile);
                builder.addPart(key, bin);

                HttpEntity reqEntity = builder.build();

                HttpEntity httpEntity = doPost(host, path, headers, queries, reqEntity, HttpEntityContainer::getEntity);
                EntityUtils.consume(httpEntity);

                headers.remove("Range");
                tmpFile.delete();
                //将本次请求的结束位置，替换成下次请求的开始位置
                start = start + size;
                Date endDate = new Date();
                long time = endDate.getTime() - startDate.getTime();
                System.out.println("用时====" + time);
            }

            futureCallback.completed(new BasicClassicHttpResponse(HttpStatus.SC_OK));
        } catch (IOException e) {
            futureCallback.cancelled();
            futureCallback.failed(e);
        } finally {
            close(fos, rw);
        }
    }

    public static ClassicHttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, Map<String, String> bodies) {
        return doPost(host, path, headers, queries, bodies, null);
    }

    /**
     * POST请求体为字符串
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body 请求体
     * @return ClassicHttpResponse
     */
    public static <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, HttpClientResponseHandler<T> responseHandler) {
        return doPost(host, path, headers, queries, new StringEntity(body, Charset.defaultCharset()), responseHandler);
    }

    public static <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, HttpEntity httpEntity, HttpClientResponseHandler<T> responseHandler) {
        HttpPost request = new HttpPost(buildUrl(host, path, queries));
        builderHeader(headers, request);
        if (httpEntity != null) {
            request.setEntity(httpEntity);
        }
        return doRequest(responseHandler, request);
    }

    public static ClassicHttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        return doPost(host, path, headers, queries, body, null);
    }

    public static String doPostHander(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        return doPost(host, path, headers, queries, body, new BasicHttpClientResponseHandler());
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
    public static <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, byte[] body, HttpClientResponseHandler<T> responseHandler) {
        return doPost(host, path, headers, queries, new ByteArrayEntity(body, ContentType.APPLICATION_OCTET_STREAM), responseHandler);
    }

    public static HttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, byte[] body) {
        return doPost(host, path, headers, queries, body, null);
    }

    public static boolean isUrl(String url) {
        // 转换为小写
        url = url.toLowerCase();
        // https、http、ftp、rtsp、mms
        String regex = "^((https|http|ftp|rtsp|mms)?://)"
            // ftp的user@
            + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"
            // IP形式的URL- 例如：199.194.52.184
            + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"
            + "|" // 允许IP和DOMAIN（域名）
            + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
            + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
            + "[a-z]{2,6})" // first level domain- .com or .museum
            + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
            + "((/?)|" // a slash isn't required if there is no file name
            + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return url.matches(regex);
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
            if (!path.startsWith(StrPoolConstant.SLASH)) {
                path += StrPoolConstant.SLASH;
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
            sbUrl.append(StrPoolConstant.QUESTION).append(sbQuery);
        }
        return sbUrl.toString();
    }

    /**
     * 拼接URL用户名和密码
     *
     * @param url
     * @param username 用户名
     * @param password
     * @return
     */
    public static String getUserInfo(String url, String username, String password) {
        try {
            if (username == null && password == null) {
                return url;
            } else {
                URIBuilder uriBuilder = (new URIBuilder(url)).setUserInfo(username + ':' + password);
                return uriBuilder.toString();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测响应体
     *
     * @param httpResponse 响应体
     * @return String
     */
    public static String checkResponseAndGetResultV2(ClassicHttpResponse httpResponse, boolean isEnsure) {
        if (httpResponse == null) {
            throw new RuntimeException();
        }

        if (isEnsure && HttpStatus.SC_OK != httpResponse.getCode()) {
            throw new RuntimeException();
        }
        HttpEntity entity = httpResponse.getEntity();
        try {
            return EntityUtils.toString(entity, Charset.defaultCharset());
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测响应体获取相应流
     *
     * @param httpResponse 响应体
     * @return
     */
    public static byte[] checkResponseStreamAndGetResult(ClassicHttpResponse httpResponse) {
        if (Objects.isNull(httpResponse)) {
            throw new NullPointerException();
        }

        if (HttpStatus.SC_OK != httpResponse.getCode()) {
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
                        URLEncoder.encode(k.toString(), CharsetUtil.defaultCharsetName()),
                        URLEncoder.encode(v.toString(), CharsetUtil.defaultCharsetName())));
                }
                sb.append("&");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });

        return sb.toString();
    }

    public static String urlEncodeWithUtf8(String s) {
        return urlEncode(s, CharsetUtil.UTF_8);
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
    public static byte[] checkResponseStreamAndGetResult(ClassicHttpResponse httpResponse, List<Integer> statusList) {
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
    public static String checkResponseAndGetResult(ClassicHttpResponse httpResponse, List<Integer> statusList) {
        checkCode(httpResponse, statusList);

        HttpEntity entity = httpResponse.getEntity();
        try {
            return EntityUtils.toString(entity, Charset.defaultCharset());
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测状态码
     *
     * @param httpResponse 响应体
     * @param statusList 检测状态表
     */
    private static void checkCode(ClassicHttpResponse httpResponse, List<Integer> statusList) {
        if (httpResponse == null) {
            throw new RuntimeException();
        }
        if (!statusList.contains(httpResponse.getCode())) {
            throw new RuntimeException();
        }
    }

    /**
     * 解析响应体
     *
     * @param httpResponse 响应体
     * @return String
     */
    public static String checkResponseAndGetResult(ClassicHttpResponse httpResponse) {
        return checkResponseAndGetResult(httpResponse, ImmutableList.of(HttpStatus.SC_OK));
    }

    public static String checkResponseAndGetResult(HttpResponse httpResponse) {
        return checkResponseAndGetResult((ClassicHttpResponse)httpResponse, ImmutableList.of(HttpStatus.SC_OK));
    }

    public static String checkResponseAndGetResult(HttpResponse httpResponse, Boolean isEnsure) {
        return checkResponseAndGetResultV2((ClassicHttpResponse)httpResponse, isEnsure);
    }

    public void addRequestInterceptorFirst(HttpRequestInterceptor httpRequestInterceptor) {
        HTTP_CLIENT_BUILDER.addRequestInterceptorFirst(httpRequestInterceptor);
        refresh();
    }

    public void addExecInterceptorAfter(final String existing, final String name, final ExecChainHandler interceptor) {
        HTTP_CLIENT_BUILDER.addExecInterceptorAfter(existing, name, interceptor);
        refresh();
    }
}
