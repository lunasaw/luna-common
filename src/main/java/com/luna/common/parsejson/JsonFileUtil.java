package com.luna.common.parsejson;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Package: com.luna.nicehash.util
 * @ClassName: JsonFileUtil
 * @Author: luna
 * @CreateTime: 2021/1/10 20:05
 * @Description:
 */
public class JsonFileUtil<T> {

    /**
     * 写入JSON文件并覆盖
     *
     * @param file
     * @param json
     */
    public static void writeSetting(String file, String json) {
        writeSetting(file, json, false);
    }

    /**
     * 写入JSON文件
     * 
     * @param file
     * @param json
     * @param append 是否追加写入 true 追加 false 覆盖
     */
    public static void writeSetting(String file, String json, boolean append) {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(new File(file), json,
                StandardCharsets.UTF_8, append);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读JSON文件转为对象列表
     * 
     * @param c
     * @param jsonFile
     * @return
     */
    public static List<?> readFileToList(Class c, String jsonFile) {
        String text = null;
        try {
            InputStream inputStream = new FileInputStream(jsonFile);
            text = IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.parseArray(text, c);
    }

    /**
     * 读JSON文件转为对象
     * 
     * @param c
     * @param jsonFile
     * @return
     */
    public static <T> T readFileToObject(Class c, String jsonFile) {
        String text = null;
        try {
            InputStream inputStream = new FileInputStream(jsonFile);
            text = IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.parseObject(text, (Type)c);
    }
}
