/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.luna.common.net.high;

import java.io.File;
import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.Future;
import com.luna.common.net.HttpUtils;
import com.luna.common.net.async.CustomAbstacktFutureCallback;
import com.luna.common.net.async.CustomAsyncHttpResponse;
import com.luna.common.net.async.CustomResponseConsumer;
import com.luna.common.net.hander.AsyncHttpClientResponseHandler;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.entity.FileEntityProducer;
import org.apache.hc.core5.http.nio.entity.PathEntityProducer;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityProducer;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import javax.annotation.PreDestroy;

/**
 * Example of asynchronous HTTP/1.1 request execution.
 */
public class AsyncHttpUtils {

    private static CloseableHttpAsyncClient     asyncClient;

    private static final HttpAsyncClientBuilder HTTP_ASYNC_CLIENT_BUILDER = HttpAsyncClients.custom();

    static {
        init();
        asyncClient.start();
    }

    @PreDestroy
    public void destroy() throws IOException {
        asyncClient.close();
    }

    public static void init() {
        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
            .setSoTimeout(Timeout.ofSeconds(HttpUtils.SOCKET_TIME_OUT))
            .build();

        final PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder.create()
            .setDefaultTlsConfig(TlsConfig.custom()
                .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_1)
                .build())
            .setConnectionConfigResolver(route -> {
                // Use different settings for all secure (TLS) connections
                if (route.isSecure()) {
                    return ConnectionConfig.custom()
                        .setConnectTimeout(Timeout.ofMinutes(2))
                        .setSocketTimeout(Timeout.ofMinutes(2))
                        .setValidateAfterInactivity(TimeValue.ofMinutes(1))
                        .setTimeToLive(TimeValue.ofHours(1))
                        .build();
                } else {
                    return ConnectionConfig.custom()
                        .setConnectTimeout(Timeout.ofMinutes(1))
                        .setSocketTimeout(Timeout.ofMinutes(1))
                        .setValidateAfterInactivity(TimeValue.ofSeconds(15))
                        .setTimeToLive(TimeValue.ofMinutes(15))
                        .build();
                }
            })
            .setTlsConfigResolver(host -> {
                // Use different settings for specific hosts
                if (host.getSchemeName().equalsIgnoreCase("localhost")) {
                    return TlsConfig.custom()
                        .setSupportedProtocols(TLS.V_1_3)
                        .setHandshakeTimeout(Timeout.ofSeconds(10))
                        .build();
                } else {
                    return TlsConfig.DEFAULT;
                }
            })
            .build();

        final CharCodingConfig codingConfig = CharCodingConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .build();

        asyncClient = HTTP_ASYNC_CLIENT_BUILDER.setIOReactorConfig(ioReactorConfig)
            .setConnectionManager(connectionManager)
            .setCharCodingConfig(codingConfig)
            .evictExpiredConnections()
            .evictIdleConnections(TimeValue.ofSeconds(HttpUtils.CONNECT_TIMEOUT))
            .setHttp1Config(Http1Config.DEFAULT)
            .build();
    }

    public static void setAuth(String host, String user, String password) {
        setAuth(host, 80, user, password);
    }

    public static void setAuth(String host, Integer port, String user, String password) {
        CredentialsProvider provider = CredentialsProviderBuilder.create()
            .add(new HttpHost(host, port), user, password.toCharArray())
            .build();
        HTTP_ASYNC_CLIENT_BUILDER.setDefaultCredentialsProvider(provider);
        asyncClient = HTTP_ASYNC_CLIENT_BUILDER.build();
        asyncClient.start();
    }

    public static <T> CustomAsyncHttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, Path file, AsyncHttpClientResponseHandler<T> responseHandler) throws IOException {
        AsyncRequestProducer producer =
            getProducer(host, path, headers, queries, new PathEntityProducer(file, StandardOpenOption.READ), Method.POST.toString());
        return doAsyncRequest(responseHandler, producer);
    }

    public static <T> CustomAsyncHttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, File file, AsyncHttpClientResponseHandler<T> responseHandler) {
        AsyncRequestProducer producer = getProducer(host, path, headers, queries, new FileEntityProducer(file), Method.POST.toString());
        return doAsyncRequest(responseHandler, producer);
    }

    public static <T> CustomAsyncHttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, AsyncHttpClientResponseHandler<T> responseHandler) {
        AsyncRequestProducer producer = getProducer(host, path, headers, queries, new StringAsyncEntityProducer(body), Method.POST.toString());
        return doAsyncRequest(responseHandler, producer);
    }

    public static <T> CustomAsyncHttpResponse doGet(String host, String path, Map<String, String> headers,
        Map<String, String> queries, AsyncHttpClientResponseHandler<T> responseHandler) {
        AsyncRequestProducer producer = getProducer(host, path, headers, queries, null, Method.GET.toString());
        return doAsyncRequest(responseHandler, producer);
    }

    private static AsyncRequestProducer getProducer(String host, String path, Map<String, String> headers, Map<String, String> queries,
                                                    AsyncEntityProducer entityProducer,
                                                    String method) {
        AsyncRequestBuilder builder = AsyncRequestBuilder.create(method);
        builder.setHttpHost(HttpHighLevelUtil.getHost(host));
        builder.setUri(HttpUtils.buildUrl(host, path, queries));
        HttpUtils.builderHeader(headers, builder);
        builder.setEntity(entityProducer);
        return builder.build();
    }

    private static <T> CustomAsyncHttpResponse doAsyncRequest(AsyncHttpClientResponseHandler<T> responseHandler, AsyncRequestProducer producer) {
        final Future<CustomAsyncHttpResponse> future;
        try {
            future = asyncClient.execute(
                producer,
                CustomResponseConsumer.create(), HttpUtils.CLIENT_CONTEXT, new CustomAbstacktFutureCallback<CustomAsyncHttpResponse>() {
                    @Override
                    public void completed(CustomAsyncHttpResponse result) {
                        responseHandler.handleResponse(result);
                    }
                });
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}