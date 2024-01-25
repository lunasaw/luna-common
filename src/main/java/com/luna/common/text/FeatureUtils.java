package com.luna.common.text;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.luna.common.constant.StrPoolConstant;

/**
 * Created by weidian2015090105 on 15/9/27.
 */
public class FeatureUtils {

    public static final String FEATURE_SEPERATOR   = ";";
    public static final String KEY_VALUE_SEPERATOR = ":";

    /**
     * 设置一个feature。如果已经存在，则替换；如果不存在，则添加。origFeatures可以为空，直接返回aKey:aValue。
     * 
     * @param origFeatures 原始的features字段。可以为空。
     * @param aKey 需要设置的feature的key
     * @param aValue 需要设置的feature的value
     * @return 返回添加或修改后的整个features字段
     */
    public static String addOrUpdateAFeature(String origFeatures, String aKey, String aValue) {

        if (null == aKey || aKey.trim().isEmpty()) {
            return origFeatures;
        }

        if (null == aValue || aValue.trim().isEmpty()) {
            return origFeatures;
        }

        if (origFeatures == null || origFeatures.isEmpty()) {
            return aKey + KEY_VALUE_SEPERATOR + aValue;
        }

        List<String[]> featureList = convertToList(origFeatures);
        for (Iterator<String[]> itor = featureList.iterator(); itor.hasNext();) {
            String[] sa = itor.next();
            if (aKey.equals(sa[0])) {
                sa[1] = aValue;
                return convertFromCollection(featureList);
            }
        }

        featureList.add(new String[] {aKey, aValue});
        return convertFromCollection(featureList);

    }

    /**
     * 删除一个feature。
     * 
     * @param origFeatures 原始features。
     * @param aKey 待删除的feature的key。
     * @return 返回删除后的features
     */
    public static String removeAFeature(String origFeatures,
        String aKey) {

        if (StringUtils.isBlank(origFeatures) || StringUtils.isBlank(aKey)) {
            return origFeatures;
        }

        List<String[]> featureList = convertToList(origFeatures);
        for (Iterator<String[]> itor = featureList.iterator(); itor.hasNext();) {
            String[] sa = itor.next();
            if (aKey.equals(sa[0])) {
                itor.remove();
                return convertFromCollection(featureList);
            }
        }

        return origFeatures;

    }

    /**
     * 从featureStr中获得指定key的value。如果需要获得一个字符串中的多个feature，建议使用convertToMap()方法。不要重复调用getAFeature。
     * 
     * @param featureStr
     * @param key
     * @return
     */
    public static String getAFeature(String featureStr, String key) {
        if (StringUtils.isBlank(featureStr)) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(featureStr, FEATURE_SEPERATOR);

        while (st.hasMoreTokens()) {
            String kv = st.nextToken();

            int kvSeperatorIndex = kv.indexOf(KEY_VALUE_SEPERATOR); // index of the key-value seperator.

            if (kvSeperatorIndex > 0) {
                String k = kv.substring(0, kvSeperatorIndex);
                if (k.equals(key)) {
                    String value = null;
                    if (kvSeperatorIndex + 1 < kv.length()) { // deal IndexOutOfBound
                        value = kv.substring(kvSeperatorIndex + 1);
                    } else {
                        value = "";
                    }
                    return value;
                }
            }
        }

        return null;
    }

    /**
     * 从featureStr中获得指定key的Long型value，注意有可能抛出NumberFormatException
     * 
     * @param featureStr
     * @param key
     * @return
     */
    public static Long getAFeatureLong(String featureStr, String key) {
        String value = getAFeature(featureStr, key);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return Long.valueOf(value);
    }

    /**
     * 获得所有feature的Map。
     * 如果feature为空，返回空的Map。
     * 
     * @return
     */
    public static Map<String, String> convertToMap(String featureStr) {
        if (StringUtils.isBlank(featureStr)) {
            return new HashMap<String, String>();
        }

        Map<String, String> featureMap = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(featureStr, FEATURE_SEPERATOR);

        while (st.hasMoreTokens()) {
            String kv = st.nextToken();

            int kvSeperatorIndex = kv.indexOf(KEY_VALUE_SEPERATOR); // index of the key-value seperator.

            if (kvSeperatorIndex > 0) {
                String key = kv.substring(0, kvSeperatorIndex);
                String value = null;
                if (kvSeperatorIndex + 1 < kv.length()) { // deal IndexOutOfBound
                    value = kv.substring(kvSeperatorIndex + 1);
                } else {
                    value = "";
                }
                featureMap.put(key, value);
            }
        }

        return featureMap;

    }

    public static Map<String, String> convertToMap(String featureStr, String seperator) {
        if (StringUtils.isBlank(featureStr)) {
            return new HashMap<String, String>();
        }

        Map<String, String> featureMap = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(featureStr, seperator);

        while (st.hasMoreTokens()) {
            String kv = st.nextToken();

            int kvSeperatorIndex = kv.indexOf(KEY_VALUE_SEPERATOR); // index of the key-value seperator.

            if (kvSeperatorIndex > 0) {
                String key = kv.substring(0, kvSeperatorIndex);
                String value = null;
                if (kvSeperatorIndex + 1 < kv.length()) { // deal IndexOutOfBound
                    value = kv.substring(kvSeperatorIndex + 1);
                } else {
                    value = "";
                }
                featureMap.put(key, value);
            }
        }

        return featureMap;

    }

    public static String convertFromMap(Map<String, String> featureMap) {
        if (featureMap == null)
            return null;
        if (featureMap.size() == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> e : featureMap.entrySet()) {
            if (!first) {
                sb.append(FEATURE_SEPERATOR);
            } else {
                first = false;
            }
            sb.append(e.getKey()).append(KEY_VALUE_SEPERATOR).append(e.getValue());
        }

        return sb.toString();
    }

    public static String convertFromCollection(Collection<String[]> featureCollection) {
        if (featureCollection == null)
            return null;
        if (featureCollection.size() == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String[] sa : featureCollection) {
            if (!first) {
                sb.append(FEATURE_SEPERATOR);
            } else {
                first = false;
            }
            sb.append(sa[0]).append(KEY_VALUE_SEPERATOR).append(sa[1]);
        }
        return sb.toString();
    }

    /**
     * List of String[]{key, value}。转化成list的好处是能保持各feature的顺序。如果输入是空，返回空的List。
     * 
     * @param featureStr
     * @return
     */
    public static List<String[]> convertToList(String featureStr) {
        if (StringUtils.isBlank(featureStr)) {
            return new ArrayList<String[]>();
        }

        List<String[]> featureList = new ArrayList<String[]>();
        StringTokenizer st = new StringTokenizer(featureStr, FEATURE_SEPERATOR);

        while (st.hasMoreTokens()) {
            String kv = st.nextToken();

            int kvSeperatorIndex = kv.indexOf(KEY_VALUE_SEPERATOR); // index of the key-value seperator.

            if (kvSeperatorIndex > 0) {
                String key = kv.substring(0, kvSeperatorIndex);
                String value = null;
                if (kvSeperatorIndex + 1 < kv.length()) { // deal IndexOutOfBound
                    value = kv.substring(kvSeperatorIndex + 1);
                } else {
                    value = "";
                }
                featureList.add(new String[] {key, value});
            }
        }

        return featureList;
    }

    /**
     * List 转换成 String
     * 
     * @param interList
     * @return
     */
    public static String convertList2Str(List<?> interList) {
        if (CollectionUtils.isEmpty(interList)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.join(interList, StrPoolConstant.COMMA);
    }

    public static List<Long> convertStr2LongList(String str) {
        if (StringUtils.isEmpty(str)) {
            return Lists.newArrayList();
        }
        return Arrays.stream(StringUtils.split(str, StrPoolConstant.COMMA))
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        Map<String, String> stepPrice = new HashMap<String, String>();

        stepPrice.put("stepPrice", "1:100:65*1:200:45");

        System.out.print(convertFromMap(stepPrice));
    }

}
