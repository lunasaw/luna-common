package com.luna.common.net.high;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.impl.bootstrap.HttpRequester;
import org.apache.hc.core5.http.impl.bootstrap.RequesterBootstrap;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;

import com.luna.common.net.HttpUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @description
 * @date 2023/4/21
 */
@Slf4j
public class HttpHighLevelUtil {

    public static final RequesterBootstrap BOOTSTRAP = RequesterBootstrap.bootstrap();
    public static HttpRequester            HTTP_REQUESTER;

    static {
        init();
    }

    public static void init() {
        HTTP_REQUESTER = BOOTSTRAP.setStreamListener(new HttpStreamListener())
            .setMaxTotal(HttpUtils.MAX_CONN)
            .setDefaultMaxPerRoute(HttpUtils.MAX_ROUTE)
            .create();
    }

    public static void setProxy(int port) {
        setProxy(StringUtils.EMPTY, port);
    }

    public static void setProxy(String hostname, int port) {
        SocketConfig.Builder custom = SocketConfig.custom();
        custom.setSoTimeout(HttpUtils.SOCKET_TIME_OUT, TimeUnit.SECONDS);
        if (StringUtils.isNotBlank(hostname)) {
            custom.setSocksProxyAddress(new InetSocketAddress(hostname, port));
            BOOTSTRAP.setSocketConfig(custom.build());
        }
        custom.setSocksProxyAddress(new InetSocketAddress(port));
        BOOTSTRAP.setSocketConfig(custom.build());
        HTTP_REQUESTER = BOOTSTRAP.create();
    }

    public static HttpHost getHost(String host) {
        try {
            return HttpHost.create(host);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T doGet(String host, String path, Map<String, String> headers,
        Map<String, String> queries, HttpClientResponseHandler<T> responseHandler) {
        HttpHost httpHost = getHost(host);
        HttpGet request = new HttpGet(HttpUtils.buildUrl(host, path, queries));
        HttpUtils.builderHeader(headers, request);
        return doRequest(responseHandler, httpHost, request);
    }

    public static <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, HttpClientResponseHandler<T> responseHandler) {
        HttpHost httpHost = getHost(host);
        HttpPost request = new HttpPost(HttpUtils.buildUrl(host, path, queries));
        HttpUtils.builderHeader(headers, request);
        return doRequest(responseHandler, httpHost, request);
    }

    public static <T> T doDelete(String host, String path, Map<String, String> headers,
        Map<String, String> queries, HttpClientResponseHandler<T> responseHandler) {
        HttpHost httpHost = getHost(host);
        HttpDelete request = new HttpDelete(HttpUtils.buildUrl(host, path, queries));
        HttpUtils.builderHeader(headers, request);
        return doRequest(responseHandler, httpHost, request);
    }

    public static <T> T doPut(String host, String path, Map<String, String> headers,
        Map<String, String> queries, HttpClientResponseHandler<T> responseHandler) {
        HttpHost httpHost = getHost(host);
        HttpPut request = new HttpPut(HttpUtils.buildUrl(host, path, queries));
        HttpUtils.builderHeader(headers, request);
        return doRequest(responseHandler, httpHost, request);
    }

    public static <T> T doRequest(HttpClientResponseHandler<T> responseHandler, HttpHost httpHost, HttpUriRequestBase httpUriRequestBase) {
        try {
            return HTTP_REQUESTER.execute(httpHost, httpUriRequestBase, Timeout.ofSeconds(HttpUtils.CONNECT_TIMEOUT), HttpUtils.CLIENT_CONTEXT,
                responseHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
