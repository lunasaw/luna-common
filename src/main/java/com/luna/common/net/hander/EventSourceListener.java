package com.luna.common.net.hander;

import com.luna.common.net.sse.Event;
import com.luna.common.net.sse.SseResponse;
import org.apache.hc.core5.concurrent.FutureCallback;

public interface EventSourceListener<T, R> extends FutureCallback<T> {

    /**
     * 单个事件
     * @param event
     */
    void onEvent(T event);


    void failed(Exception ex);

    void cancelled();
}
