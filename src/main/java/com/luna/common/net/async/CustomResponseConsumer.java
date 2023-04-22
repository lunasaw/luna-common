/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.luna.common.net.async;

import java.io.IOException;

import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncEntityConsumer;
import org.apache.hc.core5.http.nio.support.AbstractAsyncResponseConsumer;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * HTTP response consumer that generates a {@link CustomAsyncHttpResponse} instance based on events
 * of an incoming data stream.
 * <p>
 * IMPORTANT: {@link CustomAsyncHttpResponse}s are intended for simple scenarios where entities inclosed
 * in responses are known to be small. It is generally recommended to use streaming
 * {@link org.apache.hc.core5.http.nio.AsyncResponseConsumer}s, for instance, such as based on
 * {@link AbstractCharResponseConsumer} or {@link AbstractBinResponseConsumer}.
 *
 * @since 5.0
 *
 * @see SimpleBody
 * @see CustomAsyncHttpResponse
 * @see AbstractCharResponseConsumer
 * @see AbstractBinResponseConsumer
 */
public final class CustomResponseConsumer extends AbstractAsyncResponseConsumer<CustomAsyncHttpResponse, byte[]> {

    CustomResponseConsumer(final AsyncEntityConsumer<byte[]> entityConsumer) {
        super(entityConsumer);
    }

    public CustomResponseConsumer(Supplier<AsyncEntityConsumer<byte[]>> dataConsumerSupplier) {
        super(dataConsumerSupplier);
    }

    public static CustomResponseConsumer create() {
        return new CustomResponseConsumer(new CustomAsyncEntityConsumer());
    }

    @Override
    public void informationResponse(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
    }

    @Override
    protected CustomAsyncHttpResponse buildResult(final HttpResponse response, final byte[] entity, final ContentType contentType) {
        final CustomAsyncHttpResponse simpleResponse = CustomAsyncHttpResponse.copy(response);
        if (entity != null) {
            simpleResponse.setBody(entity, contentType);
        }
        return simpleResponse;
    }

}