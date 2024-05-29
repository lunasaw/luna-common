package com.luna.common.engine.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * detailNode的存储类
 */
@Getter
@Setter
public class NodeChain {

    private Map<NodeName, NodeConf> nodeMap = Maps.newLinkedHashMap();

    private static NodeName getNodeName(String groupName, Class nodeClass) {
        NodeName nodeName;
        if (StringUtils.isNotBlank(groupName)) {
            nodeName = new NodeName(groupName, nodeClass.getName());
        } else {
            nodeName = new NodeName(null, nodeClass.getName());
        }
        return nodeName;
    }

    public void add(String groupName, Class nodeClass, NodeConf nodeConf) {
        NodeName nodeName = getNodeName(groupName, nodeClass);
        add(nodeName, nodeConf);
    }

    public void add(Class nodeName, NodeConf nodeConf) {
        add(nodeName.getName(), nodeName, nodeConf);
    }

    public void add(NodeName nodeName, NodeConf nodeConf) {
        if (nodeMap.containsKey(nodeName)) {
            return;
        }
        nodeMap.put(nodeName, nodeConf);
    }

    public void replace(String groupName, Class nodeClass, NodeConf nodeConf) {
        NodeName nodeName = getNodeName(groupName, nodeClass);

        nodeMap.put(nodeName, nodeConf);
    }

    public void replace(NodeName nodeName, NodeConf nodeConf) {
        nodeMap.put(nodeName, nodeConf);
    }

    public void replace(Class nodeName, NodeConf nodeConf) {
        replace(nodeName.getName(), nodeName, nodeConf);
    }

    public void remove(Class nodeName) {
        remove(nodeName.getName(), nodeName);
    }

    public void remove(String groupName, Class nodeClass) {
        NodeName nodeName = getNodeName(groupName, nodeClass);
        nodeMap.remove(nodeName);
    }

    public Set<String> getNodeNameList() {
        return getNodeList().stream().map(NodeName::getNodeName).collect(Collectors.toSet());
    }

    public Set<NodeName> getNodeList() {
        return nodeMap.keySet();
    }

    public NodeChain deepClone() {
        LinkedHashMap<NodeName, NodeConf> cloneMap = new LinkedHashMap<>(nodeMap);
        NodeChain nodeChain = new NodeChain();
        nodeChain.setNodeMap(cloneMap);
        return nodeChain;
    }
}
