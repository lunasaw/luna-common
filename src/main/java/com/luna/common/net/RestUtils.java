package com.luna.common.net;

import java.util.Map;

import com.luna.common.text.MapUtils;
import org.apache.http.HttpResponse;

import com.google.common.collect.Maps;

/**
 * @author Luna
 */
public class RestUtils {

    public static String doGet(String host, String path, Map<String, String> headers,
        Map<String, String> queries, boolean isEnsure) {
        HttpResponse httpResponse = HttpUtils.doGet(host, path, headers, queries);
        return HttpUtils.checkResponseAndGetResult(httpResponse, isEnsure);
    }

    public static String doGet(String host, String path, Map<String, String> headers,
        Map<String, String> queries) {
        HttpResponse httpResponse = HttpUtils.doGet(host, path, headers, queries);
        return HttpUtils.checkResponseAndGetResult(httpResponse, false);
    }

    public static String doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        if (MapUtils.isEmpty(headers)) {
            headers = Maps.newHashMap();
        }
        headers.put(HttpContentTypeEnum.CONTENT_TYPE_JSON.getKey(), HttpContentTypeEnum.CONTENT_TYPE_JSON.getValue());
        HttpResponse httpResponse = HttpUtils.doPost(host, path, headers, queries, body);
        return HttpUtils.checkResponseAndGetResult(httpResponse, false);
    }

    public static String doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body, boolean isEnsure) {
        if (MapUtils.isEmpty(headers)) {
            headers = Maps.newHashMap();
        }
        headers.put(HttpContentTypeEnum.CONTENT_TYPE_JSON.getKey(), HttpContentTypeEnum.CONTENT_TYPE_JSON.getValue());
        HttpResponse httpResponse = HttpUtils.doPost(host, path, headers, queries, body);
        return HttpUtils.checkResponseAndGetResult(httpResponse, isEnsure);
    }
}
