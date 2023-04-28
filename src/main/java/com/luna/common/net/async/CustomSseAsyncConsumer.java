package com.luna.common.net.async;

import com.luna.common.net.hander.AbstactEventFutureCallback;
import com.luna.common.net.hander.EventSourceListener;
import com.luna.common.net.sse.Event;
import com.luna.common.net.sse.SseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.async.methods.AbstractCharResponseConsumer;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author luna
 * @description
 * @date 2023/4/28
 */
@Slf4j
public class CustomSseAsyncConsumer extends AbstractCharResponseConsumer<SseResponse> {

    private SseResponse                response;

    private final BlockingQueue<Event> events;

    private final AbstactEventFutureCallback<SseResponse, Event> callback;

    private final Integer              timeWait;

    public CustomSseAsyncConsumer(Integer capacity, AbstactEventFutureCallback<SseResponse, Event> callback, Integer timeWait) {
        this.events = new ArrayBlockingQueue<>(capacity);
        this.callback = callback;
        this.timeWait = timeWait;
    }

    public CustomSseAsyncConsumer(FutureCallback<Event> listener) {
        this.events = new ArrayBlockingQueue<>(100);
        this.callback = new AbstactEventFutureCallback<SseResponse, Event>() {
            @Override
            public void onEvent(Event result) {
                listener.completed(result);
            }
        };
        this.timeWait = 1000;
    }

    @Override
    protected void start(HttpResponse response, ContentType contentType) throws HttpException, IOException {
        // = onOpen
        this.response = new SseResponse(response, contentType);
        this.response.getEntity().setEvents(events);
    }

    @Override
    protected int capacityIncrement() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected void data(CharBuffer data, boolean endOfStream) throws IOException {
        // 这是一条记录
        response.getEntity().pushBuffer(data, endOfStream);
        // = onEvent
        try {
            Event poll = events.poll(timeWait, TimeUnit.MILLISECONDS);
            if (poll != null) {
                callback.onEvent(poll);
            }
        } catch (InterruptedException e) {
            log.error("data::data = {}, endOfStream = {} ", data, endOfStream, e);
        }
    }

    @Override
    protected SseResponse buildResult() throws IOException {
        return response;
    }

    @Override
    public void releaseResources() {
        callback.completed(response);
    }

    @Override
    public void failed(Exception cause) {
        callback.failed(cause);
    }
}
