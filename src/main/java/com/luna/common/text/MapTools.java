package com.luna.common.text;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * @author luna
 * 2021/8/16
 */
public class MapTools {

    private static final Logger log = LoggerFactory.getLogger(MapTools.class);

    public static Object getParam(String paramName, Map<String, Object> mapParam, Class<?> clazz) {
        Map<String, Object> map = checkMap(paramName, mapParam);

        if (map == null) {
            return null;
        }

        try {
            if (clazz == String.class) {
                return map.get(paramName).toString();
            } else if (clazz == Date.class) {
                return new Date(Long.parseLong(map.get(paramName).toString()));
            } else if (clazz == Long.class) {
                return Long.parseLong(map.get(paramName).toString());
            } else if (clazz == Integer.class) {
                return Integer.valueOf(map.get(paramName).toString());
            }
        } catch (Exception e) {
            log.error("error in get param={}, hit={}", paramName, mapParam.toString(), e);
        }
        return null;
    }

    public static Map<String, Object> checkMap(String paramName, Map<String, Object> map) {

        if (MapUtils.isEmpty(map)) {
            return null;
        }

        Object o = map.get(paramName);
        if (ObjectUtils.isEmpty(o)) {
            return null;
        }

        return map;
    }
}
