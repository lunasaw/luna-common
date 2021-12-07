package com.luna.common.text;

import com.alibaba.fastjson.JSONObject;

import java.util.Optional;

/**
 * @author fengshuai01@weidian.com
 * @description 扩展字段工具类
 * @create 2020-07-19 3:57 下午
 */
public class ExtendUtils {

    /**
     * 为防止同步问题，加上synchronized
     *
     * @param key
     * @param value
     */
    public static synchronized String setFeaturesValue(String extend, String key, Object value) {
        JSONObject extendObj;
        if (StringUtils.isBlank(extend)) {
            extendObj = new JSONObject();
        } else {
            extendObj = JSONObject.parseObject(extend);
        }
        extendObj.put(key, value);
        extend = extendObj.toString();
        return extend;
    }

    public static synchronized String removeFeaturesValue(String extend, String key) {
        JSONObject extendObj;
        if (StringUtils.isBlank(extend)) {
            return null;
        } else {
            extendObj = JSONObject.parseObject(extend);
        }
        extendObj.remove(key);
        extend = extendObj.toString();
        return extend;
    }

    public static String getFeaturesValue(String extend, String key) {
        if (StringUtils.isBlank(extend)) {
            return null;
        }
        JSONObject featuresObj = JSONObject.parseObject(extend);
        Object value = featuresObj.get(key);

        return value == null ? null : value.toString();
    }

    public static Boolean getBoolean(String flag, Boolean defaultValue) {
        return Optional.ofNullable(flag).filter(StringUtils::isNotBlank).map(Boolean::valueOf).orElse(defaultValue);
    }

    public static String getString(String flag, String defaultValue) {
        return Optional.ofNullable(flag).filter(StringUtils::isNotBlank).orElse(defaultValue);
    }

    public static Integer getInteger(String flag, Integer defaultValue) {
        return Optional.ofNullable(flag).filter(StringUtils::isNotBlank).map(Integer::parseInt).orElse(defaultValue);
    }
}
