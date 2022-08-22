package com.luna.common.utils;

import com.luna.common.file.FileNameUtil;
import org.junit.Test;

/**
 * @author luna
 * 2022/8/22
 */
public class FileToolsTest {

    @Test
    public void atest() {
        String subPath = FileNameUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb/ccc/");
        org.junit.Assert.assertEquals("ccc/", subPath);

        subPath = FileNameUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc/");
         org.junit.Assert.assertEquals("ccc/", subPath);

        subPath = FileNameUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc/test.txt");
         org.junit.Assert.assertEquals("ccc/test.txt", subPath);

        subPath = FileNameUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb/ccc");
         org.junit.Assert.assertEquals("ccc", subPath);

        subPath = FileNameUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb/ccc");
         org.junit.Assert.assertEquals("ccc", subPath);

        subPath = FileNameUtil.subPath("d:/aaa/bbb", "d:/aaa/bbb");
         org.junit.Assert.assertEquals("", subPath);

        subPath = FileNameUtil.subPath("d:/aaa/bbb/", "d:/aaa/bbb");
         org.junit.Assert.assertEquals("", subPath);
    }
    
}
