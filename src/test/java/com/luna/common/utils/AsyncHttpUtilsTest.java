package com.luna.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityProducer;
import org.junit.Test;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.luna.common.net.HttpUtilsConstant;
import com.luna.common.net.async.CustomAbstacktFutureCallback;
import com.luna.common.net.async.CustomAsyncHttpResponse;
import com.luna.common.net.async.CustomSseAsyncConsumer;
import com.luna.common.net.hander.AsyncValidatingResponseHandler;
import com.luna.common.net.high.AsyncHttpUtils;
import com.luna.common.net.sse.SseResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @description
 * @date 2023/4/22
 */
@Slf4j
public class AsyncHttpUtilsTest {

    public static void main(final String[] args) throws Exception {

    }

    @Test
    public void async_test() throws Exception {

        final HttpHost target = new HttpHost("httpbin.org");
        final String[] requestUris = new String[] {"/ip", "/user-agent", "/headers"};

        for (final String requestUri : requestUris) {
            final SimpleHttpRequest request = SimpleRequestBuilder.get()
                .setHttpHost(target)
                .setPath(requestUri)
                .build();

            System.out.println("Executing request " + request);

            CustomAsyncHttpResponse httpResponse =
                AsyncHttpUtils.doGet("http://httpbin.org", requestUri, null, null, new AsyncValidatingResponseHandler<String>() {
                    @Override
                    public <R extends HttpResponse> void handleResponse(R response) {
                        CustomAsyncHttpResponse response1 = (CustomAsyncHttpResponse)response;
                        System.out.println(response1.getBodyText());
                    }
                });

            System.out.println(httpResponse.getBodyText());
        }

        System.out.println("Shutting down");
    }

    @Test
    public void auth_test() {
        AsyncHttpUtils.setProxy(7890);
        AsyncHttpUtils.setAuth("https://httpbin.org", "user", "passwd");
        AsyncHttpUtils.basicAuth("user", "passwd", "https://httpbin.org");
        CustomAsyncHttpResponse httpResponse =
            AsyncHttpUtils.doGet("https://httpbin.org", "/basic-auth/user/passwd", null, null, new AsyncValidatingResponseHandler<String>() {});
        System.out.println(JSON.toJSONString(httpResponse));
    }

    @Test
    public void atest() {
        AsyncHttpUtils.setProxy(7890);
        Map<String, String> header = Maps.newHashMap();
        header.put(HttpHeaders.AUTHORIZATION, "Bearer sk-xxxx");
        header.put(HttpHeaders.CONTENT_TYPE, HttpUtilsConstant.JSON);

        StringAsyncEntityProducer stringAsyncEntityProducer = new StringAsyncEntityProducer(
            "{\"temperature\":0,\"model\":\"text-davinci-003\",\"prompt\":\"Say this is a test\",\"stream\":true,\"max_tokens\":7}");
        AsyncRequestProducer producer = AsyncHttpUtils.getProducer("https://api.openai.com", "/v1/completions", header, new HashMap<>(),
            stringAsyncEntityProducer, Method.POST.toString());

        CustomSseAsyncConsumer customSseAsyncConsumer = new CustomSseAsyncConsumer();

        AsyncHttpUtils.doAsyncRequest(producer, customSseAsyncConsumer, new CustomAbstacktFutureCallback<SseResponse>() {});
    }

    @Test
    public void test_sse() {
        CustomSseAsyncConsumer customSseAsyncConsumer = new CustomSseAsyncConsumer();

        ImmutableMap<String, String> map = ImmutableMap.of();
        AsyncRequestProducer producer =
            AsyncHttpUtils.getProducer("http://localhost:6060", "/stream-sse-mvc", map, new HashMap<>(), Method.GET.toString());

        SseResponse sseResponse = AsyncHttpUtils.doAsyncRequest(producer, customSseAsyncConsumer, new CustomAbstacktFutureCallback<SseResponse>() {
            @Override
            public void completed(SseResponse result) {
                super.completed(result);
            }
        });
        System.out.println(JSON.toJSONString(sseResponse));
    }

}
