//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.luna.common.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author luna
 */
public class LineSplitUtils {

    private static final int      SEPARATOR_LENGTH_LIMIT = 100;
    private static final String[] SEPARATOR_CHARS        = new String[] {"。", "！", "？", "!", "?", "."};

    public LineSplitUtils() {}

    /**
     * 分割字符串
     * 
     * @param line 字符串
     * @return
     */
    public static List<String> smartSplit(String line) {
        List<String> list = new ArrayList<>();
        if (line.length() <= SEPARATOR_LENGTH_LIMIT) {
            list.add(line);
            return list;
        } else {
            splitAndAdd(list, line, 0);
            List<String> resultList = new ArrayList<>();

            for (String item : list) {
                if (item.length() <= SEPARATOR_LENGTH_LIMIT) {
                    resultList.add(item);
                } else {
                    slipByComma(resultList, item);
                }
            }

            return resultList;
        }
    }

    private static void splitAndAdd(List<String> list, String line, int charPosi) {
        String mark = SEPARATOR_CHARS[charPosi];
        String[] items = StringUtils.split(line, mark);
        String[] var5 = items;
        int var6 = items.length;

        for (int var7 = 0; var7 < var6; ++var7) {
            String item = var5[var7];
            if (items.length > 1 && notEndWithSeparator(item)) {
                item = item + mark;
            }

            if (charPosi == SEPARATOR_CHARS.length - 1) {
                list.add(item);
            } else if (item.length() <= SEPARATOR_LENGTH_LIMIT) {
                list.add(item);
            } else {
                splitAndAdd(list, item, charPosi + 1);
            }
        }

    }

    private static void slipByComma(List<String> resultList, String item) {
        String[] subItems = StringUtils.split(item, "，,");
        if (subItems.length == 1) {
            resultList.add(item);
        } else {
            String subRes = subItems[0] + "，";

            for (int i = 1; i < subItems.length; ++i) {
                String sub = subItems[i];
                if (sub.length() != 0) {
                    if (notEndWithSeparator(sub)) {
                        sub = sub + "，";
                    }

                    if (subRes.length() + sub.length() > SEPARATOR_LENGTH_LIMIT) {
                        resultList.add(subRes);
                        subRes = sub;
                    } else {
                        subRes = subRes + sub;
                    }
                }
            }

            if (subRes.length() > 0) {
                resultList.add(subRes);
            }

        }
    }

    private static boolean notEndWithSeparator(String str) {
        return !ArrayUtils.contains(SEPARATOR_CHARS, str.substring(str.length() - 1));
    }
}
