package com.luna.common.net;

import com.google.common.collect.Maps;
import com.luna.common.net.hander.ValidatingResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author luna
 */
@Slf4j
public class HttpConnectionPoolUtil {

    private static Logger                                                logger                     =
        LoggerFactory.getLogger(HttpConnectionPoolUtil.class);

    /**
     * 监控线程
     */
    private static final int                                             MONITOR_MAX_CONN           = 20;
    /**
     * 发送请求的客户端单例
     */
    private static final Map<String, CloseableHttpClient>                HTTP_CLIENT_CONCURRENT_MAP = Maps.newConcurrentMap();

    private static final Map<String, PoolingHttpClientConnectionManager> CONCURRENT_MANAGER_MAP     = Maps.newConcurrentMap();

    /**
     * 开启监控线程,对异常和空闲线程进行关闭
     */
    private static final ScheduledExecutorService                        MONITOR_EXECUTOR           =
        new ScheduledThreadPoolExecutor(MONITOR_MAX_CONN, r -> new Thread(r, "httpclient-monitor"));

    /**
     * 相当于线程锁,用于线程安全
     */
    private final static Object                                          SYNC_LOCK                  = new Object();

    /**
     * 对http请求进行基本设置
     *
     * @param httpRequestBase http请求
     */
    private static void setRequestConfig(HttpRequestBase httpRequestBase) {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(HttpUtils.CONNECT_TIMEOUT)
            .setConnectTimeout(HttpUtils.CONNECT_TIMEOUT)
            .setSocketTimeout(HttpUtils.SOCKET_TIMEOUT).build();

        httpRequestBase.setConfig(requestConfig);
    }

    public static CloseableHttpClient getHttpClient(String hostName) {
        CloseableHttpClient httpClient = HTTP_CLIENT_CONCURRENT_MAP.get(hostName);
        if (httpClient != null) {
            return httpClient;
        }
        // 多线程下多个线程同时调用getHttpClient容易导致重复创建httpClient对象的问题,所以加上了同步锁
        synchronized (SYNC_LOCK) {
            httpClient = createHttpClient(hostName);
            HTTP_CLIENT_CONCURRENT_MAP.put(hostName, httpClient);
        }
        return httpClient;
    }

    /**
     * 根据host和port构建httpclient实例
     *
     * @param host 要访问的域名+端口
     * @return
     */
    public static CloseableHttpClient createHttpClient(String host) {
        ConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", plainSocketFactory)
            .register("https", sslSocketFactory).build();

        PoolingHttpClientConnectionManager manager = CONCURRENT_MANAGER_MAP.get(host);
        if (manager == null) {
            manager = new PoolingHttpClientConnectionManager(registry);
            // 设置连接参数
            // 最大连接数
            manager.setMaxTotal(HttpUtils.MAX_CONN);
            // 路由最大连接数
            manager.setDefaultMaxPerRoute(HttpUtils.MAX_ROUTE);

            HttpHost httpHost = HttpHost.create(host);
            manager.setMaxPerRoute(new HttpRoute(httpHost), HttpUtils.MAX_ROUTE);
        }

        // 请求失败时,进行请求重试
        HttpRequestRetryHandler handler = (e, i, httpContext) -> {
            if (i > 3) {
                // 重试超过3次,放弃请求
                logger.error("retry has more than 3 time, give up request");
                return false;
            }
            if (e instanceof NoHttpResponseException) {
                // 服务器没有响应,可能是服务器断开了连接,应该重试
                logger.error("receive no response from server, retry");
                return true;
            }
            if (e instanceof SSLHandshakeException) {
                // SSL握手异常
                logger.error("SSL hand shake exception");
                return false;
            }
            if (e instanceof InterruptedIOException) {
                // 超时
                logger.error("InterruptedIOException");
                return false;
            }
            if (e instanceof UnknownHostException) {
                // 服务器不可达
                logger.error("server host unknown");
                return false;
            }
            if (e instanceof SSLException) {
                logger.error("SSLException");
                return false;
            }

            HttpClientContext context = HttpClientContext.adapt(httpContext);
            HttpRequest request = context.getRequest();
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                // 如果请求不是关闭连接的请求
                return true;
            }
            return false;
        };

        CloseableHttpClient client = HttpClients.custom().setConnectionManager(manager).setRetryHandler(handler).build();
        PoolingHttpClientConnectionManager finalManager = manager;
        MONITOR_EXECUTOR.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 关闭异常连接
                finalManager.closeExpiredConnections();
                // 关闭5s空闲的连接
                finalManager.closeIdleConnections(HttpUtils.SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
            }
        }, 20, 50, TimeUnit.MILLISECONDS);
        CONCURRENT_MANAGER_MAP.put(host, manager);
        return client;
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
    public static <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, ValidatingResponseHandler<T> handler) {
        return HttpUtils.doPost(host, path, headers, queries, body, handler, true);
    }

    public static <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, ResponseHandler<T> responseHandler) {
        return HttpUtils.doPost(host, path, headers, queries, body, responseHandler, true);
    }

    public static <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, byte[] body, ResponseHandler<T> responseHandler) {
        return HttpUtils.doPost(host, path, headers, queries, body, responseHandler, true);
    }

    public static <T> T doGet(String host, String path, Map<String, String> headers,
        Map<String, String> queries, ResponseHandler<T> responseHandler) {
        return HttpUtils.doGet(host, path, headers, queries, responseHandler, true);
    }

    public static <T> T doDelete(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, ResponseHandler<T> responseHandler) {
        return HttpUtils.doDelete(host, path, headers, queries, body, responseHandler, true);
    }

    public static <T> T doPut(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, ResponseHandler<T> responseHandler) {
        return HttpUtils.doPut(host, path, headers, queries, body, responseHandler, true);
    }

    public static <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, Map<String, String> bodies, ResponseHandler<T> responseHandler) {
        return HttpUtils.doPost(host, path, headers, queries, bodies, responseHandler, true);
    }

    /**
     * 关闭连接池
     */
    @PreDestroy
    public static void closeConnectionPool() {
        HTTP_CLIENT_CONCURRENT_MAP.forEach((s, closeableHttpClient) -> {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                log.error("closeConnectionPool:: ", e);
            }
        });

        CONCURRENT_MANAGER_MAP.forEach((s, manager) -> manager.close());
        MONITOR_EXECUTOR.shutdown();
    }
}
