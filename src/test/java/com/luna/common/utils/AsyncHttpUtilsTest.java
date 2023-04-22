package com.luna.common.utils;

import com.luna.common.net.HttpUtils;
import com.luna.common.net.async.CustomAbstacktFutureCallback;
import com.luna.common.net.async.CustomAsyncHttpResponse;
import com.luna.common.net.async.CustomResponseConsumer;
import com.luna.common.net.hander.AsyncValidatingResponseHandler;
import com.luna.common.net.high.AsyncHttpUtils;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Future;

/**
 * @author weidian
 * @description
 * @date 2023/4/22
 */
public class AsyncHttpUtilsTest {

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

            CustomAsyncHttpResponse httpResponse = AsyncHttpUtils.doGet("http://httpbin.org", requestUri, null, null, new AsyncValidatingResponseHandler<String>() {
                @Override
                public void handleResponse(CustomAsyncHttpResponse response) {
                    // call back do it
                    System.out.println(response.getBodyText());
                }
            });

            System.out.println(httpResponse.getBodyText());
        }

        System.out.println("Shutting down");
    }

    public static class CustomFutureCallback extends CustomAbstacktFutureCallback<String>{
        @Override
        public void completed(String result) {
            System.out.println(result);
        }
    }

    public static void main(final String[] args) throws Exception {


    }

}
