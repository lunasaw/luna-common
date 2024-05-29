package com.luna.common.engine.task;

import com.luna.common.engine.model.EngineContext;
import com.luna.common.engine.model.EngineRunData;

/**
 *
 * 业务节点类，有一系列的EngineNode组合而成
 */
public interface EngineNode<T> {

    /**
     * Node的执行方法
     * 
     * @param nodeData nodeData
     * @param engineContext engineContext
     */
    T invokeNode(EngineRunData nodeData, EngineContext engineContext);

    /**
     * node执行完后执行的方法
     *
     * @param nodeData nodeData
     * @param engineContext engineContext
     */
    void afterInvoke(EngineRunData nodeData, EngineContext engineContext);

    /**
     * 从EngineContext中获取此node结果的key
     *
     * @return String
     */
    String resultKey();

    /**
     * 是否可以执行,按照上下文控制
     *
     * @param engineContext
     * @return
     */
    boolean couldContinueExecute(EngineContext engineContext);

}
