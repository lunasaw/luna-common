package com.luna.common.net.high;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.impl.Http1StreamListener;

/**
 * @author luna
 * @description
 * @date 2023/4/21
 */
@Slf4j
public class HttpStreamListener implements Http1StreamListener {
    @Override
    public void onRequestHead(final HttpConnection connection, final HttpRequest request) {
        log.info("onRequestHead::connection = {}, request = {}", connection, request);
    }

    @Override
    public void onResponseHead(final HttpConnection connection, final HttpResponse response) {
        log.info("onResponseHead::connection = {}, response = {}", connection, response);
    }

    @Override
    public void onExchangeComplete(final HttpConnection connection, final boolean keepAlive) {
        log.info("onExchangeComplete::connection = {}, keepAlive = {}", connection, keepAlive);
    }
}
