package com.luna.common.utils;

import static com.luna.common.net.HttpUtils.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.utils.Base64;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.luna.common.net.HttpUtils;
import com.luna.common.net.HttpUtilsConstant;

public class HttpUtilsTest {

    @Before
    public void before() {

    }

    @Test
    public void test1Get() throws Exception {
        ClassicHttpResponse httpResponse = HttpUtils.doGet("http://www.baidu.com", null, null, null);
        Assert.assertEquals(200, httpResponse.getCode());
        HttpEntity entity = httpResponse.getEntity();
        String responseString = EntityUtils.toString(entity, "utf-8");
        Assert.assertNotNull(responseString);
    }

    @Test
    public void sse_test() {

        String host = "https://www.w3schools.com";
        String path = "/html/demo_sse.php";
        String s = doGet(host, path, null, null, new BasicHttpClientResponseHandler());
        System.out.println(s);
    }

    @Test
    public void get_test() {
        HttpClientResponseHandler<String> responseHandler = response -> {
            return EntityUtils.toString(response.getEntity());
        };
        String responseString = HttpUtils.doGet("https://httpbin.org", "/get", null, null, responseHandler);
        Assert.assertNotNull(responseString);
    }

    @Test
    public void get_post() {
        HttpClientResponseHandler<String> responseHandler = response -> {
            return EntityUtils.toString(response.getEntity());
        };
        String responseString = HttpUtils.doPost("https://httpbin.org", "/post", null, null, StringUtils.EMPTY, responseHandler);
        Assert.assertNotNull(responseString);
    }

    @Test
    public void get_post_2() {
        HttpClientResponseHandler<String> responseHandler = response -> {
            return EntityUtils.toString(response.getEntity());
        };
        HttpUtils.setProxy(7890);
        Map<String, String> header = Maps.newHashMap();
        header.put(HttpHeaders.AUTHORIZATION, "Bearer sk-xxxxx");
        header.put(HttpHeaders.CONTENT_TYPE, HttpUtilsConstant.JSON);

        StringEntity stringEntity = new StringEntity("{\n" +
            "    \"input\": [\n" +
            "        \"十们代存府出治对提流感形织务文。\"\n" +
            "    ],\n" +
            "    \"model\": \"text-moderation-latest\"\n" +
            "}", Charset.defaultCharset());
        String responseString =
            HttpUtils.doPost("https://api.openai.com", "/v1/moderations", header,
                null, stringEntity, responseHandler);
        Assert.assertNotNull(responseString);
    }

    @Test
    public void proxy_test() {
        HttpUtils.setProxy("127.0.0.1", 7890);
        get_post();
    }

    @Test
    public void put_test() {
        String responseString = doPutHandler("https://httpbin.org", "/put", null, null, StringUtils.EMPTY);
        Assert.assertNotNull(responseString);
    }

    @Test
    public void delete_test() {
        String responseString = doDeleteHandler("https://httpbin.org", "/delete", null, null, StringUtils.EMPTY);
        Assert.assertNotNull(responseString);
    }

    @Test
    public void auth_basic_test() {
        HttpUtils.basicAuth("user", "passwd", "https://httpbin.org");
        String responseString = doGetHandler("https://httpbin.org", "/basic-auth/user/passwd", null, null);
        Assert.assertNotNull(responseString);
    }

    @Test
    public void auth_digest_test() {
        HttpUtils.digestAuth("user", "passwd", "https://httpbin.org/");
        String responseString = doGetHandler("https://httpbin.org", "/digest-auth/auth/user/passwd", null, null);
        Assert.assertNotNull(responseString);
    }

    @Test
    public void auth_basic_header_test() {
        final String auth = "user2" + ":" + "passwd";
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
        final String authHeader = StandardAuthScheme.BASIC + " " + new String(encodedAuth);
        String responseString =
            doGetHandler("https://httpbin.org", "/basic-auth/user2/passwd", ImmutableMap.of(HttpHeaders.AUTHORIZATION, authHeader), null);
        Assert.assertNotNull(responseString);
    }
}
