package com.luna.common.text;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author luna
 * 2021/6/13
 */
public class MapUtils {


    /**
     * Map非空注入
     * @param map 集合
     * @param k k
     * @param v v
     * @return
     */
    public static Map<String, Object> putIfNull(Map<String, Object> map, String k, Object v) {
        if (StringUtils.isNotBlank(k) && ObjectUtils.isNotEmpty(v)) {
            map.put(k, v);
        }
        return map;
    }
}
