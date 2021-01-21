package com.luna.common.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;

/**
 * @author luna@mac
 * @className ParseJsonFile.java
 * @description TODO
 * @createTime 2021年01月12日 23:28:00
 */
public class ParseJsonFile<T> {

    public List<?> readListFile(Class c, String jsonFile) {
        String text = null;
        try {
            InputStream inputStream = new FileInputStream(jsonFile);
            text = IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.parseArray(text, c);
    }

    public T readFile(Class c, String jsonFile) {
        String text = null;
        try {
            InputStream inputStream = new FileInputStream(jsonFile);
            text = IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T)JSON.parseObject(text, c);
    }
}
