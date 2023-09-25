package com.luna.common.net.download;

import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/9/25
 */
@Slf4j
public class BasicDownloadTaskListener implements DownloadTaskListener{
    @Override
    public void downloadCompleted() {
        log.info("downloadCompleted::");
    }
}
