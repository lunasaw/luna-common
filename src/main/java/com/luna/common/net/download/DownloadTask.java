package com.luna.common.net.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;

import com.luna.common.net.HttpUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 多线程任务下载 需要服务端支持range
 */
@Slf4j
public class DownloadTask {

    long contentLength;
    String url;
    boolean acceptRanges;
    int threadCount;
    String localPath;
    List<Thread> threads;
    long receivedCount;
    DownloadTaskListener listener;
    CloseableHttpClient httpClient;
    boolean debug = true;

    public DownloadTask(String url, String localPath, int threadCount) {
        contentLength = -1;
        this.url = url;
        acceptRanges = false;
        this.threadCount = threadCount;
        this.localPath = localPath;
        threads = new ArrayList<Thread>();
        receivedCount = 0;
    }

    public void addDownloadTaskListener(DownloadTaskListener listener) {
        this.listener = listener;
    }

    public void startDown() throws Exception {
        try {
            getDownloadFileInfo(httpClient);
            startDownloadThread();
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    public boolean getDebug() {
        return debug;
    }

    /**
     * @return the progree between 0 and 100;return -1 if download not started
     */
    public float getDownloadProgress() {
        float progress = 0;
        if (contentLength == -1) {
            return -1;
        }
        synchronized (this) {
            progress = (float) (DownloadTask.this.receivedCount * 100.0 / contentLength);
        }
        return progress;
    }

    public long getContentLength() {
        return contentLength;
    }

    public long getDownload() {
        long download;
        synchronized (this) {
            download = DownloadTask.this.receivedCount;
        }
        return download;
    }

    /**
     * 获取下载文件信息
     */
    private void getDownloadFileInfo(CloseableHttpClient httpClient) throws Exception {
        HttpHead httpHead = new HttpHead(url);
        CloseableHttpResponse response;
        if (httpClient == null) {
            response = (CloseableHttpResponse) HttpUtils.doHead(url);
        } else {
            response = httpClient.execute(httpHead);
        }
        // 获取HTTP状态码
        int statusCode = response.getCode();

        if (statusCode == 404)
            throw new Exception("资源不存在!");
        if (getDebug()) {
            for (Header header : response.getHeaders()) {
                log.info(header.getName() + ":" + header.getValue());
            }
            log.info("-----------------------------http head----------------");
        }

        // Content-Length
        Header[] headers = response.getHeaders("Content-Length");
        if (headers.length > 0) {
            contentLength = Long.valueOf(headers[0].getValue());
            log.info("length : " + contentLength);
        }

        if (contentLength < 1024 * 100) {
            threadCount = 1;
        } else if (contentLength < 1024 * 1024) {
            threadCount = 2;
        } else if (contentLength < 1024 * 1024 * 10) {
            threadCount = 5;
        } else if (contentLength < 1024 * 1024 * 100) {
            threadCount = 10;
        } else {
            threadCount = 20;
        }
        log.info("thread Count = " + threadCount);
        httpHead.abort();
        httpHead = new HttpHead(url);
        httpHead.addHeader("Range", "bytes=0-" + (contentLength - 1));
        if (httpClient == null) {
            response = (CloseableHttpResponse) HttpUtils.doHead(url);
        } else {
            response = httpClient.execute(httpHead);
        }
        if (response.getCode() == 206) {
            acceptRanges = true;
            log.info("support range");
        } else {
            acceptRanges = false;
            log.info("not support range");
        }
        httpHead.abort();
    }

    /**
     * 启动多个下载线程
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void startDownloadThread() throws IOException,
            FileNotFoundException {
        // 创建下载文件
        final File file = new File(localPath);
        file.createNewFile();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(contentLength);
        raf.close();

        // 定义下载线程事件实现类

        final Calendar time = Calendar.getInstance();
        final long startMili = System.currentTimeMillis();// 当前时间对应的毫秒数
        final DownloadThreadListener threadListener = new DownloadThreadListener() {

            public void afterPerDown(DownloadThreadEvent event) {
                // 下载完一个片段后追加已下载字节数
                synchronized (this) {
                    DownloadTask.this.receivedCount += event.getCount();
                }
            }

            public void downCompleted(DownloadThreadEvent event) {
                // 下载线程执行完毕后从主任务中移除
                threads.remove(event.getTarget());
                if (threads.isEmpty()) {
                    long endMili = System.currentTimeMillis();
                    Calendar time1 = Calendar.getInstance();
                    log.info(file + "总耗时为：" + (endMili - startMili) + "毫秒");
                    log.info("time------------- ：" + file + time1.compareTo(time));
                    listener.downloadCompleted();
                }
                if (getDebug()) {
                    log.info("剩余线程数：" + threads.size());
                }
            }
        };

        // 不支持多线程下载时
        if (!acceptRanges) {
            log.info("不支持多线程下载时 startDownloadThread:: acceptRanges: {}", false);
            // 定义普通下载
            DownloadThread thread = new DownloadThread(url, 0, contentLength,
                    file, false);
            thread.addDownloadListener(threadListener);
            thread.start();
            threads.add(thread);
            return;
        }

        // 每个请求的大小
        long perThreadLength = contentLength / threadCount + 1;
        long startPosition = 0;
        long endPosition = perThreadLength;
        // 循环创建多个下载线程
        do {
            if (endPosition >= contentLength)
                endPosition = contentLength - 1;

            DownloadThread thread = new DownloadThread(url, startPosition,
                    endPosition, file);
            thread.addDownloadListener(threadListener);
            thread.start();
            threads.add(thread);

            startPosition = endPosition + 1;// 此处加 1,从结束位置的下一个地方开始请求
            endPosition += perThreadLength;
        } while (startPosition < contentLength);
    }
}