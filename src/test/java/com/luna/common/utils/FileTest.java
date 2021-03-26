package com.luna.common.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.luna.common.file.FileUtils;
import com.luna.common.net.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luna@mac
 * @className FileTest.java
 * @description TODO
 * @createTime 2021年03月22日 14:57:00
 */
public class FileTest {

    @Test
    public void fileTest() {
        String s = FileUtils.readFileToString("src/main/java/com/luna/common/a.txt");
        // System.out.println(s);
        char[] chars = s.toCharArray();
        List<Character> number = new ArrayList<>();
        List<Character> strings = new ArrayList<>();

        List<String> strings1 = new ArrayList<>();
        List<String> strings2 = new ArrayList<>();

        for (char aChar : chars) {
            if (aChar > 47 && aChar < 58) {
                number.add(aChar);
                strings1.add(getStr(strings));
                strings = new ArrayList<>();
            } else {
                strings.add(aChar);
                strings2.add(getStr(number));
                number = new ArrayList<>();
            }
        }

        List<String> strings3 = new ArrayList<>();
        List<String> strings4 = new ArrayList<>();
        for (String s1 : strings1) {
            if (!s1.equals("")) {
                strings3.add(s1);
            }
        }
        for (String s1 : strings2) {
            if (!s1.equals("")) {
                strings4.add(s1);
            }
        }
        System.out.println(strings3);

        Map<String, String> map = new HashMap<>();
        List<Map<String, String>> maps = new ArrayList<>();
        for (int i = 0; i < strings3.size(); i++) {
            String name = strings3.get(i).substring(0, strings3.get(i).length() - 1);
            String code = strings4.get(i);
            maps.add(ImmutableMap.of("name", name, "code", code));
        }
        FileUtils.writeStringToFile("src/main/java/com/luna/common/d.json", JSON.toJSONString(maps));

        System.out.println(maps);

    }

    public static String getStr(List<Character> number) {
        StringBuffer sb = new StringBuffer();
        for (Character character : number) {
            sb.append(character);
        }
        return sb.toString();
    }

    @Test
    public void httpTest() throws IOException {
        String s = HttpUtil.get(
            "http://apis.juhe.cn/birthEight/query?year=1999&month=11&day=7&hour=12&key=b456ab5a68fb51c242edff6501cb0905");
        System.out.println(s);

    }
}
