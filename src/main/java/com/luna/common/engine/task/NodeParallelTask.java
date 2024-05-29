package com.luna.common.engine.task;

import com.luna.common.engine.spi.NodeSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.luna.common.engine.model.EngineContext;
import com.luna.common.engine.model.EngineRunData;
import com.luna.common.exception.BaseException;

import java.util.List;

/**
 * Node节点的执行类
 */
public class NodeParallelTask extends TradeEngineCallable {

    private static final Logger    LOGGER                      = LoggerFactory.getLogger(NodeParallelTask.class);
    /**
     * 是否打node执行超过100ms日志
     */
    public static volatile boolean PRINT_NODE_EXECUTE_TIME_LOG = false;
    /**
     * 是否打印性能分析日志
     */
    public static volatile boolean PRINT_PERF_ANALYZE_LOG      = false;
    private EngineNode             engineNode;
    private EngineRunData          engineRunData;
    private EngineContext          engineContext;

    public NodeParallelTask(EngineNode engineNode, EngineRunData engineRunData, EngineContext engineContext) {
        this.engineNode = engineNode;
        this.engineRunData = engineRunData;
        this.engineContext = engineContext;
    }

    @Override
    Object _call() throws BaseException {
        return execute();
    }

    public Object execute() throws BaseException {
        long start = System.currentTimeMillis();
        try {
            // Node前置检查
            Object o = engineNode.invokeNode(engineRunData, engineContext);
            AbstractEngineNode abstractEngineNode = (AbstractEngineNode)engineNode;
            if (abstractEngineNode != null) {
                List<NodeSpi> spiList = abstractEngineNode.getSpiList();

            }
            engineNode.afterInvoke(engineRunData, engineContext);
            // 后置处理
            return o;
        } finally {
            long end = System.currentTimeMillis();
            if (PRINT_NODE_EXECUTE_TIME_LOG && (end - start) > 100) {
                LOGGER.warn("fatal error, please notice. execute node:{} exceed times " + (end - start) + " millisecond ",
                    engineNode.getClass().getName());
            }
            if (PRINT_PERF_ANALYZE_LOG) {
                LOGGER.warn("==========execute node:{} total use " + (end - start) + " millisecond ===========", engineNode.getClass().getName());
            }
        }
    }

}
