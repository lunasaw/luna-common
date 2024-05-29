package com.luna.common.utils;

import com.luna.common.net.HttpUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.luna.common.net.RestUtils;

public class RestUtilsTest {
    @Test
    public void test1Get() {
        String result = RestUtils.doGet("http://localhost:8001", "/fusion-user/api/checkUserExist", null,
            ImmutableMap.of("userMark", "1173282254@qq.com"));
        Assert.assertNotNull(result);
        Assert.assertEquals("{\"success\":true,\"code\":1,\"message\":\"success\",\"data\":true}", result);
    }

    @Test
    public void test2Get() {
        String result = RestUtils.doGet("http://localhost:8001", "/fusion-user/api/getUserIdBySessionKey", null,
            ImmutableMap.of("sessionKey", "123213123", "site", "wednesday"));
        Assert.assertNotNull(result);
        Assert.assertEquals("{\"success\":true,\"code\":1,\"message\":\"success\",\"data\":true}", result);
    }

    @Test
    public void test2Post() {
        String result = RestUtils.doPost("http://localhost:8001", "/fusion-user/api/login", null, null,
            "{\"userMark\":\"15696756582\",\"password\":\"11111111\",\"site\":\"wednesday\"}");
        Assert.assertNotNull(result);
        Assert.assertEquals("{\"success\":true,\"code\":1,\"message\":\"success\",\"data\":true}", result);
    }

    @Test
    public void atest() {
        //
    }
}
