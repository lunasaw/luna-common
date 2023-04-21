package com.luna.common.utils;

import com.luna.common.net.HttpUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class HttpUtilsTest {
    @Test
    public void test1Get() throws Exception {
        ClassicHttpResponse httpResponse = HttpUtils.doGet("http://www.baidu.com", null, null, null);
        Assert.assertEquals(200, httpResponse.getCode());
        HttpEntity entity = httpResponse.getEntity();
        String responseString = EntityUtils.toString(entity, "utf-8");
        Assert.assertNotNull(responseString);
    }
}
