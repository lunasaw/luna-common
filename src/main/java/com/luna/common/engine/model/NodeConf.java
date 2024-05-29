package com.luna.common.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 节点配置
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NodeConf {

    /**
     * 是否是强依赖
     */
    private boolean isStrongRely = true;
    /**
     * 并行执行超时时间
     * 默认200ms
     */
    private int     timeout      = 200;
}
