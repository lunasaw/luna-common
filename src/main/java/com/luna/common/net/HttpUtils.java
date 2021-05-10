package com.luna.common.net;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.luna.common.dto.constant.ResultCode;
import com.luna.common.exception.BaseException;
import com.luna.common.text.CharsetKit;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author Luna
 */
public class HttpUtils {

    private static CloseableHttpClient httpClient;

    static {
        SSLConnectionSocketFactory socketFactory = null;
        try {
            // 信任所有
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                (TrustStrategy)(chain, authType) -> true).build();
            socketFactory = new SSLConnectionSocketFactory(sslContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", socketFactory != null ? socketFactory : PlainConnectionSocketFactory.getSocketFactory())
            .build();

        // for proxy debug
        // HttpHost proxy = new HttpHost("localhost", 8888);
        // RequestConfig defaultRequestConfig =
        // RequestConfig.custom().setProxy(proxy).setSocketTimeout(5000).setConnectTimeout(5000)
        // .setConnectionRequestTimeout(5000).build();

        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000)
            .setConnectionRequestTimeout(10000).build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(200);
        httpClient =
            HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(defaultRequestConfig).build();
    }

    /**
     * get
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @return
     * @throws Exception
     */
    public static HttpResponse doGet(String host, String path, Map<String, String> headers,
        Map<String, String> queries) {
        HttpGet request = new HttpGet(buildUrl(host, path, queries));
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Post form 文件
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, Map<String, String> bodies) {
        HttpPost request = new HttpPost(buildUrl(host, path, queries));
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        if (MapUtils.isNotEmpty(bodies)) {
            List<NameValuePair> nameValuePairList = Lists.newArrayList();
            for (String key : bodies.keySet()) {
                // 传入参数可以为file或者filePath，在此处做转换
                File file = new File(bodies.get(key));
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                // 设置浏览器兼容模式
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                // 设置请求的编码格式
                builder.setCharset(Consts.UTF_8);
                builder.setContentType(ContentType.MULTIPART_FORM_DATA);
                // 添加文件
                builder.addBinaryBody(key, file);
                HttpEntity reqEntity = builder.build();
                request.setEntity(reqEntity);
                nameValuePairList.add(new BasicNameValuePair(key, bodies.get(key)));
            }
        }
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Post String
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body 请求体
     * @return HttpResponse
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, String body) {
        HttpPost request = new HttpPost(buildUrl(host, path, queries));
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, Charset.defaultCharset()));
        }
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Post stream
     *
     * @param host 主机地址
     * @param path 路径
     * @param headers 请求头
     * @param queries 请求参数
     * @param body 请求体
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, Map<String, String> headers,
        Map<String, String> queries, byte[] body) {
        HttpPost request = new HttpPost(buildUrl(host, path, queries));
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }
        try {
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建url
     * 
     * @param host 主机地址
     * @param path 路径
     * @param queries 请求参数
     * @return
     */
    private static String buildUrl(String host, String path, Map<String, String> queries) {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);

        if (StringUtils.isNotBlank(path)) {
            sbUrl.append(path);
        }

        if (MapUtils.isNotEmpty(queries)) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : queries.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && StringUtils.isNotBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (StringUtils.isNotBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (StringUtils.isNotBlank(query.getValue())) {
                        sbQuery.append("=");
                        try {
                            sbQuery.append(URLEncoder.encode(query.getValue(), CharsetKit.UTF_8));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    /**
     * 检测响应体
     *
     * @param httpResponse
     * @return
     */
    public static String checkResponseAndGetResult(HttpResponse httpResponse, boolean isEnsure) {
        if (httpResponse == null) {
            throw new RuntimeException();
        }
        if (httpResponse.getStatusLine() == null) {
            throw new RuntimeException();
        }
        if (isEnsure && HttpStatus.SC_OK != httpResponse.getStatusLine().getStatusCode()) {
            throw new RuntimeException();
        }
        HttpEntity entity = httpResponse.getEntity();
        try {
            return EntityUtils.toString(entity, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测响应体获取相应流
     *
     * @param httpResponse
     * @return
     */
    public static byte[] checkResponseStreamAndGetResult(HttpResponse httpResponse) {
        if (httpResponse == null) {
            throw new RuntimeException();
        }
        if (httpResponse.getStatusLine() == null) {
            throw new RuntimeException();
        }
        if (HttpStatus.SC_OK != httpResponse.getStatusLine().getStatusCode()) {
            throw new RuntimeException();
        }
        HttpEntity entity = httpResponse.getEntity();
        try {
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * doURL
     *
     * @param url url路径
     * @param method 方法
     * @param headers 请求头
     * @param queryParams 请求参数
     * @return
     * @throws IOException
     */
    public static JSONObject doURL(String url, String method, Map<String, String> headers,
        Map<String, String> queryParams) throws IOException {
        // url参数拼接
        if (!queryParams.isEmpty()) {
            url += "?" + HttpUtils.urlencode(queryParams);
        }
        URL realUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestMethod(method);

        // request headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        // request body
        Map<String, Boolean> methods = new HashMap<>();
        methods.put("POST", true);
        methods.put("PUT", true);
        methods.put("PATCH", true);
        Boolean hasBody = methods.get(method);
        if (hasBody != null) {
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            conn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(urlencode(queryParams));
            out.flush();
            out.close();
        }

        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String result = "";
        while ((line = in.readLine()) != null) {
            result += line;
        }
        return JSONObject.parseObject(result);
    }

    /**
     * 解析生成URL
     * 
     * @param map 键值对
     * @return 生成的URL尾部
     * @throws UnsupportedEncodingException
     */
    public static String urlencode(Map<?, ?> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                URLEncoder.encode(entry.getValue().toString(), "UTF-8")));
        }
        return sb.toString();
    }

    /**
     * 检测响应体并解析
     * 
     * @param httpResponse 响应体
     * @param statusList 状态码列表
     * @return 解析字节
     */
    public static byte[] checkResponseStreamAndGetResult(HttpResponse httpResponse, List<Integer> statusList) {
        checkCode(httpResponse, statusList);
        HttpEntity entity = httpResponse.getEntity();
        try {
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测响应体并解析
     * 
     * @param httpResponse 响应体
     * @param statusList 状态码列表
     * @return 解析字符串
     */
    public static String checkResponseAndGetResult(HttpResponse httpResponse, List<Integer> statusList) {
        checkCode(httpResponse, statusList);

        HttpEntity entity = httpResponse.getEntity();
        try {
            return EntityUtils.toString(entity, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检测状态码
     * 
     * @param httpResponse
     * @param statusList
     */
    private static void checkCode(HttpResponse httpResponse, List<Integer> statusList) {
        if (httpResponse == null) {
            throw new RuntimeException();
        }
        if (httpResponse.getStatusLine() == null) {
            throw new RuntimeException();
        }
        if (!statusList.contains(httpResponse.getStatusLine().getStatusCode())) {
            throw new RuntimeException();
        }
    }

    /**
     * 解析响应体
     * 
     * @param httpResponse
     * @return
     */
    public static String checkResponseAndGetResult(HttpResponse httpResponse) {
        return checkResponseAndGetResult(httpResponse, ImmutableList.of(HttpStatus.SC_OK));
    }

    /**
     * 读取
     *
     * @param rd
     * @return
     * @throws IOException
     */
    public static String readAll(Reader rd) {
        try {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char)cp);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new BaseException(ResultCode.ERROR_SYSTEM_EXCEPTION, e.getMessage());
        }
    }

    /**
     * 创建链接
     *
     * @param url
     * @return
     * @throws IOException
     * @throws JSONException
     */
    private static JSONObject readJsonFromUrl(String url) throws Exception {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = JSONObject.parseObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    /**
     * 检查是不是网络路径
     *
     * @param url
     * @return
     */
    public static boolean isNetUrl(String url) {
        boolean reault = false;
        if (url != null) {
            if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("rtsp")
                || url.toLowerCase().startsWith("mms")) {
                reault = true;
            }
        }
        return reault;
    }

}
