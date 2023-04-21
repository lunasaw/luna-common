package com.luna.common.net.high;

import com.luna.common.net.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.impl.Http1StreamListener;
import org.apache.hc.core5.http.impl.bootstrap.HttpRequester;
import org.apache.hc.core5.http.impl.bootstrap.RequesterBootstrap;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author weidian
 * @description
 * @date 2023/4/21
 */
@Slf4j
public class HttpHighLevelUtil {

    final static HttpRequester httpRequester = RequesterBootstrap.bootstrap()
            .setStreamListener(new HttpStreamListener() )
            .setSocketConfig(SocketConfig.custom()
                    .setSoTimeout(10, TimeUnit.SECONDS)
                    .build())
            .create();

    final static HttpCoreContext coreContext = HttpCoreContext.create();

    public static void doPost(String host, String path, Map<String,String> headers,
                       Map<String, String> queries) throws URISyntaxException, HttpException, IOException {

//        Header[] headers = header.entrySet().stream()
//                .map(entry -> new BasicHeader(entry.getKey(), entry.getValue()))
//                .toArray(BasicHeader[]::new);
        HttpHost httpHost = HttpHost.create(host);

        HttpPost request = new HttpPost(HttpUtils.buildUrl(host, path, queries));
        HttpUtils.builderHeader(headers, request);


        httpRequester.execute(httpHost, request, Timeout.ofSeconds(5), coreContext, (HttpClientResponseHandler) response -> {
            System.out.println(response.getCode() + " " + response.getReasonPhrase());

            System.out.println(EntityUtils.toString(response.getEntity()));
            System.out.println("==============");
            return null;
        });
    }

    public static void main(final String[] args) throws Exception {
        final HttpRequester httpRequester = RequesterBootstrap.bootstrap()
                .setStreamListener(new Http1StreamListener() {

                    @Override
                    public void onRequestHead(final HttpConnection connection, final HttpRequest request) {
                        System.out.println(connection.getRemoteAddress() + " " + new RequestLine(request));

                    }

                    @Override
                    public void onResponseHead(final HttpConnection connection, final HttpResponse response) {
                        System.out.println(connection.getRemoteAddress() + " " + new StatusLine(response));
                    }

                    @Override
                    public void onExchangeComplete(final HttpConnection connection, final boolean keepAlive) {
                        if (keepAlive) {
                            System.out.println(connection.getRemoteAddress() + " exchange completed (connection kept alive)");
                        } else {
                            System.out.println(connection.getRemoteAddress() + " exchange completed (connection closed)");
                        }
                    }

                })
                .setSocketConfig(SocketConfig.custom()
                        .setSoTimeout(10, TimeUnit.SECONDS)
                        .build())
                .create();

        final HttpCoreContext coreContext = HttpCoreContext.create();
        final HttpHost target = new HttpHost("httpbin.org");
        final String[] requestUris = new String[] {"/ip", "/user-agent", "/headers"};

        for (int i = 0; i < requestUris.length; i++) {
            final String requestUri = requestUris[i];
            final ClassicHttpRequest request = ClassicRequestBuilder.get()
                    .setHttpHost(target)
                    .setPath(requestUri)
                    .build();

            doPost("http://httpbin.org", requestUri, null, null);

//            try (ClassicHttpResponse response = httpRequester.execute(target, request, Timeout.ofSeconds(5), coreContext)) {
//                System.out.println(requestUri + "->" + response.getCode());
//                System.out.println(EntityUtils.toString(response.getEntity()));
//                System.out.println("==============");
//            }
//
        }
    }
}
