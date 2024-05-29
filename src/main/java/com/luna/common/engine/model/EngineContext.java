package com.luna.common.engine.model;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

@Data
public class EngineContext {

    /**
     * adaptor的结果缓存
     */
    private Map<String/** ResultKey **/
        , Object>   adaptorMap = Maps.newConcurrentMap();

    /**
     * engin是否继续执行
     */
    private boolean isStop;

}