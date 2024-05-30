package com.luna.common.engine.spi;

import com.luna.common.engine.model.EngineContext;

/**
 * 每个流程节点的扩展点
 **/
public interface BatchNodeNodeSpi<T> extends NodeSpi {
    /**
     * 判断该扩展点是否需要执行 true:需要执行; false:不需要执行
     *
     * @param spiData
     * @param context
     * @return
     */
    boolean isAccept(T spiData, EngineContext context);

    /**
     * 封装spi的业务逻辑方法
     *
     * @param context 整个流程的执行上下文
     */
    void invoke(T spiData, EngineContext context);
}
