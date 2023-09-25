package com.luna.common.utils;

import com.luna.common.net.HttpUtils;
import com.luna.common.net.download.DownloadTask;
import com.luna.common.net.download.DownloadTaskListener;
import org.junit.Test;

/**
 * @author luna
 * @date 2023/9/25
 */
public class FilePontDownloadTest{

    /**
     * <a href="https://github.com/lunasaw/luna-doc-impl/tree/main/point-upload">服务端代码</a>
     * @throws InterruptedException
     */
    @Test
    public void test_point() throws InterruptedException {
        //10个线程
        DownloadTask downloadTask = new DownloadTask("http://localhost:9000/file/download?path=/Users/weidian/Downloads/Another-Redis-Desktop-Manager.1.6.1.dmg", "/Users/weidian/Downloads/download2.dmg", 10);
        try {

            downloadTask.addDownloadTaskListener(new DownloadTaskListener() {

                @Override
                public void downloadCompleted() {
                    //下载完成
                    // TODO Auto-generated method stub
                    System.out.print("download completed");
                }
            });
            downloadTask.startDown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(30000);
    }
}
