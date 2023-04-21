/*
 * Copyright 2009-2011 Jon Stevens et al.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luna.common.net.hander;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

/**
 * Basic response handler which takes an url for documentation.
 *
 * @param <T> return type of {@link HttpClientResponseHandler#handleResponse(ClassicHttpResponse)} (HttpResponse)}.
 * @author luna
 */
@Slf4j
public abstract class ValidatingResponseHandler<T> extends AbstractHttpClientResponseHandler<T> {

    /**
     * Checks the response for a statuscode between {@link HttpStatus#SC_OK} and {@link HttpStatus#SC_MULTIPLE_CHOICES}
     * and throws an {@link RuntimeException} otherwise.
     *
     * @param response to check
     * @throws RuntimeException when the status code is not acceptable.
     */
    protected void validateResponse(HttpResponse response) {
        String reasonPhrase = response.getReasonPhrase();
        int statusCode = response.getCode();

        if (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES) {
            return;
        }
        throw new RuntimeException("Unexpected response: " + statusCode + reasonPhrase);
    }
}