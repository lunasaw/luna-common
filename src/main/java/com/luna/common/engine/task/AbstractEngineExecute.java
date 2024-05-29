package com.luna.common.engine.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luna.common.engine.model.*;
import com.luna.common.exception.BaseException;
import com.luna.common.spring.SpringBeanService;
import com.luna.common.thread.CommonThreadPoolUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: EngineNode的执行引擎，会串行执行一系列的EngineNode。同一个group的EngineNode会并行执行
 * @Author:
 * @Modified by:
 */
@Slf4j
public abstract class AbstractEngineExecute<T> {

    private static CommonThreadPoolUtil COMMON_THREAD_POOL_UTIL = new CommonThreadPoolUtil();

    /**
     * 引擎执行入口
     *
     * @param nodeChainChain
     * @param engineRunData
     * @param engineContext
     * @throws Exception
     */
    @SneakyThrows
    public T execute(NodeChain nodeChainChain, EngineRunData engineRunData, EngineContext engineContext) {

        Map<String, List<NodeName>> nodeGroup = groupByGroupName(nodeChainChain);

        Map<NodeName, NodeConf> nodeMap = nodeChainChain.getNodeMap();
        for (String groupName : nodeGroup.keySet()) {
            BaseException exp = null;
            boolean needThrowExp = false;
            List<NodeName> nodeNameList = nodeGroup.get(groupName);
            // 只有一个Node的节点，串行执行
            if (nodeNameList.size() == 1) {
                NodeName nodeName = nodeNameList.get(0);

                EngineNode detailNode = (EngineNode)SpringBeanService.getSingleBeanByType(Class.forName(nodeName.getNodeName()));
                NodeParallelTask nodeParallelTask = new NodeParallelTask(detailNode, engineRunData, engineContext);
                try {
                    Object result = nodeParallelTask.execute();
                    engineContext.getAdaptorMap().put(detailNode.resultKey(), result);
                } catch (BaseException e) {
                    needThrowExp = isStrongRely(nodeMap, nodeName);
                    if (!isStrongRely(nodeMap, nodeName)) {
                        // 注意：只有非强依赖的节点我们才打印日志，因为强依赖的节点，会把异常抛出终结流程，那么异常一定会在thor层面被捕获并且打印堆栈，会造成异常重复打印
                        // 并且由于跑出来的异常是DetailException业务日志，所以我们把异常信息打印到monitor-biz.log中
                        log.warn("detailnode occur exception !!!", e);
                    }
                    exp = e;
                } catch (Exception e) {
                    needThrowExp = isStrongRely(nodeMap, nodeName);
                    if (!isStrongRely(nodeMap, nodeName)) {
                        // 注意：只有非强依赖的节点我们才打印日志，因为强依赖的节点，会把异常抛出终结流程，那么异常一定会在thor层面被捕获并且打印堆栈，会造成异常重复打印
                        log.error("detailEngine execute throw unknown Exception 2", e);
                    }
                    exp = BaseException.SYSTEM_ERROR;
                }
            } else { // 多个个Node的组合节点，并行执行
                List<Future> resultList = new ArrayList<>();
                List<NodeName> executedNodeNameList = new ArrayList<>();
                List<NodeParallelTask> executedNodeList = new ArrayList<>();
                for (NodeName nodeName : nodeNameList) {
                    EngineNode detailNode = (EngineNode)SpringBeanService.getSingleBeanByType(Class.forName(nodeName.getNodeName()));
                    if (!detailNode.couldContinueExecute(engineContext)) {
                        continue;
                    }

                    NodeParallelTask nodeParallelTask = new NodeParallelTask(detailNode, engineRunData, engineContext);
                    executedNodeList.add(nodeParallelTask);
                    executedNodeNameList.add(nodeName);
                    resultList.add(COMMON_THREAD_POOL_UTIL.getThreadPool().submit(nodeParallelTask));
                }
                for (int i = 0; i < resultList.size(); i++) {
                    NodeName nodeName = executedNodeNameList.get(i);
                    EngineNode detailNode = (EngineNode)SpringBeanService.getSingleBeanByType(Class.forName(nodeName.getNodeName()));
                    NodeConf nodeConf = nodeMap.get(nodeName);
                    boolean strongRely = nodeConf.isStrongRely();
                    int timeout = nodeConf.getTimeout();
                    Future future = resultList.get(i);
                    try {
                        Object o = future.get(timeout, TimeUnit.MILLISECONDS);
                        engineContext.getAdaptorMap().put(detailNode.resultKey(), o);
                    } catch (ExecutionException e) {
                        needThrowExp = strongRely;
                        Throwable cause = e.getCause();
                        if (cause instanceof BaseException) {
                            if (!strongRely) {
                                // 注意：只有非强依赖的节点我们才打印日志，因为强依赖的节点，会把异常抛出终结流程，那么异常一定会在thor层面被捕获并且打印堆栈，会造成异常重复打印
                                // 并且由于跑出来的异常是DetailException业务日志，所以我们把异常信息打印到monitor-biz.log中
                                log.warn("parallel detailnode occur cexception !!!", e);
                            }
                            exp = (BaseException)cause;
                        }
                    } catch (TimeoutException e) {
                        // 超时直接打warn
                        needThrowExp = strongRely;
                        if (!strongRely) {
                            // 非强依赖的节点我们才打印日志，因为强依赖的节点，会把异常抛出终结流程，那么异常一定会在thor层面被捕获并且打印堆栈，会造成异常重复打印
                            log.warn(String.format("detailEngine execute timeout. nodeName:%s", nodeName));
                        }
                        exp = BaseException.SYSTEM_ERROR;

                    } catch (Exception e) {
                        if (!strongRely) {
                            log.error(String.format("detailEngine execute error. nodeName:%s", nodeName), e);
                        }
                        needThrowExp = strongRely;
                        exp = BaseException.SYSTEM_ERROR;
                    }
                }
            }
            if (needThrowExp) {
                throw exp;
            }
        }
        return assembleModel(engineRunData, engineContext);
    }

    /**
     * 是否强依赖节点
     * 
     * @param nodeMap
     * @param nodeKey
     * @return
     */
    private boolean isStrongRely(Map<NodeName, NodeConf> nodeMap, NodeName nodeKey) {
        return nodeMap.get(nodeKey) != null && nodeMap.get(nodeKey).isStrongRely();
    }

    public abstract T assembleModel(EngineRunData engineRunData, EngineContext context);

    /**
     * 按groupName分组, 没有group的Node放在一组，groupName相同的放到一组
     *
     * @param nodeChainChain
     * @return
     */
    private Map<String, List<NodeName>> groupByGroupName(NodeChain nodeChainChain) {
        Map<String, List<NodeName>> nodegroup = Maps.newLinkedHashMap();
        for (NodeName nodeKey : nodeChainChain.getNodeList()) {
            String groupName = nodeKey.getGroupName();
            String nodeName = nodeKey.getNodeName();

            if (StringUtils.isBlank(groupName)) {
                List<NodeName> nodeNameList = Lists.newArrayList();
                nodeNameList.add(nodeKey);
                nodegroup.put(nodeName, nodeNameList);
            } else {
                List<NodeName> nodeNameList = nodegroup.get(groupName);
                if (nodeNameList == null) {
                    nodeNameList = Lists.newArrayList();
                }
                nodeNameList.add(nodeKey);
                nodegroup.put(groupName, nodeNameList);
            }
        }
        return nodegroup;
    }
}
