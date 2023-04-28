package com.luna.common.net.method;


import org.apache.hc.client5.http.classic.methods.HttpGet;

import java.net.URI;

/**
 * Allows us to set the correct Accept header automatically and always use HTTP GET.
 */
public class SseRequest extends HttpGet {


    public SseRequest(URI uri) {
        super(uri);
        addHeader("Accept", "text/event-stream");
    }

    public SseRequest(String uri) {
        super(uri);
        addHeader("Accept", "text/event-stream");
    }
}
