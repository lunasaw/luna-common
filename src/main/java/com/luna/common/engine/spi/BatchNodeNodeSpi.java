package com.luna.common.engine.spi;

import com.luna.common.engine.model.EngineRunData;

/**
 * 每个流程节点的扩展点
 **/
public interface BatchNodeNodeSpi<T> extends NodeSpi {
    /**
     * 判断该扩展点是否需要执行 true:需要执行; false:不需要执行
     *
     * @param spiData
     * @return
     */
    default boolean isAccept(EngineRunData spiData) {
        return true;
    }

    /**
     * 封装spi的业务逻辑方法
     *
     */
    void invoke(EngineRunData spiData);
}
