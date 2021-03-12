package com.luna.common.webdav;

import com.luna.common.file.FileUtils;
import com.luna.common.net.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.client.methods.HttpMkcol;
import org.apache.jackrabbit.webdav.client.methods.HttpPropfind;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * @author luna@mac
 * @className WebDavUtils.java
 * @description TODO
 * @createTime 2021年03月12日 09:23:00
 */
public class WebDavUtils {
    /**
     * 上传文件 路径不存在
     * 
     * @param webDavConfig 配置
     * @param url 网络文件路径
     * @param fis 文件流
     * @return
     * @throws IOException
     */
    public static boolean upload(WebDavConfig webDavConfig, String url, FileInputStream fis) throws IOException {
        HttpPut put = new HttpPut(url);
        InputStreamEntity requestEntity = new InputStreamEntity(fis);
        put.setEntity(requestEntity);
        int status = webDavConfig.getClient().execute(put, webDavConfig.getContext()).getStatusLine().getStatusCode();
        return status == HttpStatus.SC_CREATED;
    }

    /**
     * 上传文件 路径不存在则递归创建目录 不能覆盖
     *
     * @param webDavConfig 配置
     * @param url 网络文件路径
     * @param fis 文件流
     * @return
     * @throws IOException
     */
    public static boolean upload(WebDavConfig webDavConfig, String url, FileInputStream fis, boolean isCreate)
        throws IOException {
        if (isCreate) {
            if (!existDir(webDavConfig, url)) {
                // 不存在目录则创建
                makeDirs(webDavConfig, url);
            }
            return upload(webDavConfig, url, fis);
        }
        return false;
    }

    /**
     * 上传文件 路径不存在则递归创建目录，文件存在则覆盖
     * 
     * @param webDavConfig 配置文件
     * @param url 网络路径
     * @param filePath 文件路径
     * @param isCreate 路径不存在是否创建文件夹
     * @param cover 是否覆盖
     * @return
     * @throws IOException
     */
    public static boolean upload(WebDavConfig webDavConfig, String url, String filePath, boolean isCreate,
        boolean cover) throws IOException {
        if (isCreate) {
            if (!existDir(webDavConfig, url)) {
                // 不存在目录则创建
                makeDirs(webDavConfig, url);
            }
            if (upload(webDavConfig, url, new FileInputStream(filePath))) {
                return true;
            } else if (cover && delete(webDavConfig, url)) {
                return upload(webDavConfig, url, new FileInputStream(filePath));
            }
        }
        return false;
    }

    /**
     * 递归创建文件
     * 
     * @param webDavConfig 配置
     * @param path 文件网络路径
     * @throws IOException
     */
    public static boolean makeDirs(WebDavConfig webDavConfig, String path) throws IOException {
        path = path.replace(webDavConfig.getUri().toString(), StringUtils.EMPTY);
        String[] dirs = path.split("/");
        if (path.lastIndexOf(".") > 0) {
            dirs = Arrays.copyOf(dirs, dirs.length - 1);
        }
        StringBuilder stringBuilder = new StringBuilder(webDavConfig.getUri().toString());
        for (String string : dirs) {
            stringBuilder.append(string + "/");
            if (existDir(webDavConfig, stringBuilder.toString())) {
                continue;
            }
            if (makeDir(webDavConfig, stringBuilder.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断文件或者文件夹是否存在
     * 
     * @param webDavConfig
     * @param url 路径
     * @return
     * @throws IOException
     */
    public static boolean existDir(WebDavConfig webDavConfig, String url) throws IOException {
        HttpPropfind propfind = new HttpPropfind(url, DavConstants.PROPFIND_BY_PROPERTY, 1);
        HttpResponse response = webDavConfig.getClient().execute(propfind, webDavConfig.getContext());
        int statusCode = response.getStatusLine().getStatusCode();
        return DavServletResponse.SC_MULTI_STATUS == statusCode;
    }

    /**
     * 删除文件或者文件夹
     * 
     * @param webDavConfig
     * @param url 文件路径
     * @return
     * @throws IOException
     */
    public static Boolean delete(WebDavConfig webDavConfig, String url) throws IOException {
        HttpDelete delete = new HttpDelete(url);
        int statusCode =
            webDavConfig.getClient().execute(delete, webDavConfig.getContext()).getStatusLine().getStatusCode();
        return HttpStatus.SC_NO_CONTENT == statusCode;
    }

    /**
     * 创建文件夹
     * 
     * @param webDavConfig 配置文件
     * @param url 路径
     * @return
     * @throws IOException
     */
    private static boolean makeDir(WebDavConfig webDavConfig, String url) throws IOException {
        HttpMkcol mkcol = new HttpMkcol(url);
        int retCode =
            webDavConfig.getClient().execute(mkcol, webDavConfig.getContext()).getStatusLine().getStatusCode();
        return retCode == HttpStatus.SC_CREATED;
    }

    /**
     * 
     * @param webDavConfig
     * @param url
     * @param filePath
     * @throws IOException
     */
    public static void download(WebDavConfig webDavConfig, String url, String filePath) throws IOException {
        HttpGet get = new HttpGet(url);
        HttpResponse response = webDavConfig.getClient().execute(get, webDavConfig.getContext());
        byte[] bytes = HttpUtils.checkResponseStreamAndGetResult(response);
        FileUtils.writeBytesToFile(bytes, filePath);
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        WebDavConfig webDavConfig =
            new WebDavConfig().setUsername("luna").setPassword("czy1024").setUri(new URI("http://f.xicc.cc:22317/"));
        webDavConfig.initClientContext();
        System.out.println(upload(webDavConfig, "http://f.xicc.cc:22317/httpd/logo/luna.jpg",
            "/Users/luna_mac/Pictures/img/logo/logo-luna.png", true, true));
    }

}
