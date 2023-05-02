package com.luna.common.net.method;

import java.net.URI;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

/**
 * @author luna@mac
 * 2021年05月12日 09:28
 */
public class HttpDelete extends HttpUriRequestBase {

    public final static String METHOD_NAME = "DELETE";

    public HttpDelete(String uri) {
        super(METHOD_NAME, URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
