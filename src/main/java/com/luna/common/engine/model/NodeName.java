package com.luna.common.engine.model;

import com.luna.common.check.Assert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 节点Key
 */
@NoArgsConstructor
@Data
public class NodeName {

    /**
     * 节点组
     */
    private String groupName;
    /**
     * 节点名称
     */
    private String nodeName;

    public NodeName(String groupName, String nodeName) {
        Assert.notNull(nodeName, "节点名称不能为空");
        this.groupName = groupName;
        this.nodeName = nodeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NodeName nodeName1 = (NodeName)o;
        return Objects.equals(groupName, nodeName1.groupName) && Objects.equals(nodeName, nodeName1.nodeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, nodeName);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NodeName{");
        sb.append("groupName='").append(groupName).append('\'');
        sb.append(", nodeName='").append(nodeName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
