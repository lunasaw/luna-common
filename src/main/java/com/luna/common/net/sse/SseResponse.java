package com.luna.common.net.sse;

import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;

public class SseResponse extends BasicClassicHttpResponse {

    private final HttpResponse original;
    private final SseEntity    entity;

    public SseResponse(HttpResponse original, ContentType contentType) {
        super(original.getCode());
        this.original = original;
        this.entity = new SseEntity(contentType);
    }

    @Override
    public SseEntity getEntity() {
        return entity;
    }

    public HttpResponse getOriginal() {
        return original;
    }
}
