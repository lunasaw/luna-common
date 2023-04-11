package com.luna.common.net.hander;

import org.apache.http.HttpResponse;

/**
 * {@link org.apache.http.client.ResponseHandler} which just executes the request and checks the answer is
 * in the valid range of {@link ValidatingResponseHandler#validateResponse(HttpResponse)}.
 *
 * @author mirko
 */
public class HttpResponseHandler extends ValidatingResponseHandler<HttpResponse> {


    @Override
    public HttpResponse handleResponse(HttpResponse httpResponse) {
        this.validateResponse(httpResponse);
        return httpResponse;
    }
}
