package com.luna.common.text;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @description 扩展字段工具类
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
        if (StringTools.isBlank(extend)) {
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
        if (StringTools.isBlank(extend)) {
            return null;
        } else {
            extendObj = JSONObject.parseObject(extend);
        }
        extendObj.remove(key);
        extend = extendObj.toString();
        return extend;
    }

    public static String getFeaturesValue(String extend, String key) {
        if (StringTools.isBlank(extend)) {
            return null;
        }
        JSONObject featuresObj = JSONObject.parseObject(extend);
        Object value = featuresObj.get(key);

        return value == null ? null : value.toString();
    }

    public Object getFeaturesSubValue(String extend, String key, String subKey) {
        if (StringUtils.isBlank(extend)) {
            return null;
        }
        JSONObject featuresObj = JSONObject.parseObject(extend);
        Object object = featuresObj.get(key);
        if (object != null) {
            return ((JSONObject) object).get(subKey);
        }
        return object;
    }

    public synchronized String setFeaturesSubValue(String extend, String key, String subKey, Object value) {
        JSONObject extendObj;
        if (StringUtils.isBlank(extend)) {
            extendObj = new JSONObject();
        } else {
            extendObj = JSONObject.parseObject(extend);
        }
        JSONObject object = new JSONObject();
        object.put(subKey, value);
        extendObj.put(key, object);
        return extendObj.toString();
    }

    public synchronized String removeFeaturesSubValue(String extend, String key, String subKey) {
        JSONObject extendObj;
        if (StringTools.isBlank(extend)) {
            return null;
        } else {
            extendObj = Optional.ofNullable(JSONObject.parseObject(extend)).orElse(new JSONObject());
        }
        JSONObject object = Optional.ofNullable(JSONObject.parseObject(extendObj.getString(key))).orElse(new JSONObject());
        object.remove(subKey);
        extendObj.put(key, object);
        return extendObj.toString();
    }

    public static Boolean getBoolean(String flag, Boolean defaultValue) {
        return Optional.ofNullable(flag).filter(StringTools::isNotBlank).map(Boolean::valueOf).orElse(defaultValue);
    }

    public static String getString(String flag, String defaultValue) {
        return Optional.ofNullable(flag).filter(StringTools::isNotBlank).orElse(defaultValue);
    }

    public static Integer getInteger(String flag, Integer defaultValue) {
        return Optional.ofNullable(flag).filter(StringTools::isNotBlank).map(Integer::parseInt).orElse(defaultValue);
    }
}
