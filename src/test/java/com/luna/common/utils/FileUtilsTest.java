package com.luna.common.utils;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileUtilsTest {
    @Test
    public void test1DownloadFile() {
        FileUtils.downloadFile(
            "http://ftp.cuhk.edu.hk/pub/packages/apache.org//commons/io/binaries/commons-io-2.6-bin.tar.gz",
            "d:/commons-io-2.6-bin.tar.gz");
    }

    @Test
    public void test2CheckFileWithSHA256() {
        Assert.assertTrue(FileUtils.checkFileWithSHA256("d:/commons-io-2.6-bin.tar.gz",
            "b6ba5fb49c5f6406dbabec2b77205c032b0b2ade6c217d20a9819a121fdaf3db"));
    }
}
