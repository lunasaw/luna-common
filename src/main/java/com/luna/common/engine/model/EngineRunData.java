package com.luna.common.engine.model;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luna
 */
@Data
public class EngineRunData {

    private Map<String, Object> runData = new ConcurrentHashMap<>();
}