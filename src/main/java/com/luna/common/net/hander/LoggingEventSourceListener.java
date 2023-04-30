package com.luna.common.net.hander;

import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingEventSourceListener<T, E> extends AbstactEventFutureCallback<T, E> {

    @Override
    public void onEvent(E result) {
        log.info("onEvent::result = {}", JSON.toJSONString(result));
    }

    @Override
    public void completed(T result) {}

    @Override
    public void failed(Exception ex) {
        super.failed(ex);
    }

    @Override
    public void cancelled() {
        super.cancelled();
    }
}
