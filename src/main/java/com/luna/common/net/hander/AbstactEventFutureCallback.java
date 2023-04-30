package com.luna.common.net.hander;

import org.apache.hc.core5.concurrent.FutureCallback;

import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @description
 * @date 2023/4/28
 */
@Slf4j
public abstract class AbstactEventFutureCallback<T, E> implements FutureCallback<T> {

    public void onEvent(E result) {
        log.info("onEvent::result = {}", JSON.toJSONString(result));
    }

    @Override
    public void completed(T result) {
        log.info("completed::result = {}", JSON.toJSONString(result));
    }

    @Override
    public void failed(Exception ex) {

    }

    @Override
    public void cancelled() {

    }
}
