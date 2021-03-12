package com.luna.common.utils;

import com.luna.common.net.RestUtils;
import org.junit.Assert;

import com.google.common.collect.ImmutableMap;

public class RestUtilsTest {
    // @Test
    public void test1Get() {
        String result = RestUtils.doGet("http://localhost:8081", "/fusion-user/api/checkUserMark", null,
            ImmutableMap.of("userMark", "15696756582@163.com"));
        Assert.assertNotNull(result);
        Assert.assertEquals("{\"success\":true,\"code\":1,\"message\":\"success\",\"data\":false}", result);
    }

    // @Test
    public void test2Post() {
        String result = RestUtils.doPost("http://localhost:8081", "/fusion-user/api/login", null, null,
            "{\"userMark\":\"15696756584\",\"password\":\"22222222\",\"site\":\"wednesday\"}");
        Assert.assertNotNull(result);
        Assert.assertEquals("{\"success\":true,\"code\":1,\"message\":\"success\",\"data\":true}", result);
    }
}
