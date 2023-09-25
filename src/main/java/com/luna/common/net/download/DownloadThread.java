package com.luna.common.net.download;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

import com.google.common.collect.ImmutableMap;
import com.luna.common.net.HttpUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadThread extends Thread {

    String url;
    long startPosition;
    long endPosition;
    boolean isRange;
    File file;
    DownloadThreadListener listener;
    long downloaded;
    CloseableHttpClient httpClient;

    public DownloadThread(String url, long startPosition, long endPosition, boolean isRange, File file, DownloadThreadListener listener, long downloaded, CloseableHttpClient httpClient) {
        this.url = url;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.isRange = isRange;
        this.file = file;
        this.listener = listener;
        this.downloaded = downloaded;
        this.httpClient = httpClient;
    }

    void addDownloadListener(DownloadThreadListener listener) {
        this.listener = listener;
    }

    public long getdownLoaded() {
        return this.downloaded;
    }

    DownloadThread(String url, long startPosition, long endPosition, File file) {
        this.url = url;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.isRange = true;
        this.file = file;
        this.downloaded = 0;
    }

    DownloadThread(String url, long startPosition, long endPosition, File file,
                   boolean isRange) {
        this.url = url;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.isRange = isRange;
        this.file = file;
        this.downloaded = 0;
    }

    public void run() {
        try {
            HttpGet httpGet = new HttpGet(url);
            if (isRange) {// 多线程下载
                httpGet.addHeader("Range", "bytes=" + startPosition + "-"
                        + endPosition);
            }
            CloseableHttpResponse response;
            if (httpClient == null) {
                response = (CloseableHttpResponse) HttpUtils.doGet(url, ImmutableMap.of("Range", "bytes=" + startPosition + "-"
                        + endPosition));
            } else {
                response = httpClient.execute(httpGet);
            }
            int statusCode = response.getCode();
            if (statusCode == 206 || (statusCode == 200 && !isRange)) {
                java.io.InputStream inputStream = response.getEntity()
                        .getContent();
                RandomAccessFile outputStream = new RandomAccessFile(file, "rw");
                outputStream.seek(startPosition);
                int count = 0;
                byte[] buffer = new byte[10240];
                while ((count = inputStream.read(buffer, 0, buffer.length)) > 0) {
                    outputStream.write(buffer, 0, count);
                    downloaded += count;
                    listener.afterPerDown(new DownloadThreadEvent(this, count));
                }
                outputStream.close();
            }
            httpGet.abort();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            listener.downCompleted(new DownloadThreadEvent(this, endPosition));
            log.info("End:" + startPosition + "-" + endPosition);
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("run:: ", e);
            }
        }
    }
}