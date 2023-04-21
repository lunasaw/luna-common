package com.luna.common.net.hander;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpResponse;

/**
 * {@link org.apache.http.client.ResponseHandler} which just executes the request and checks the answer is
 * in the valid range of {@link ValidatingResponseHandler#validateResponse(HttpResponse)}.
 *
 * @author mirko
 */
public class HttpResponseHandler extends ValidatingResponseHandler<ClassicHttpResponse> {

    @Override
    public ClassicHttpResponse handleResponse(ClassicHttpResponse response) {
        this.validateResponse(response);
        return response;
    }
}
