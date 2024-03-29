package com.luna.common.net;

import java.util.Map;

import org.apache.hc.core5.http.io.HttpClientResponseHandler;

/**
 * @author luna
 * @description
 * @date 2023/4/11
 */
public interface HttpRequestInterface {

    <T> T doGet(String host, String path, Map<String, String> headers, Map<String, String> queries, HttpClientResponseHandler<T> responseHandler);

    <T> T doDelete(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, HttpClientResponseHandler<T> responseHandler);

    <T> T doPut(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, HttpClientResponseHandler<T> responseHandler);

    <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, Map<String, String> bodies, HttpClientResponseHandler<T> responseHandler);

    <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, HttpClientResponseHandler<T> responseHandler);

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
    <T> T doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, byte[] body, HttpClientResponseHandler<T> responseHandler);
}
