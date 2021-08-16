package com.luna.common.os.hardware;

/**
 * @author Tony
 */
public class ProcessorDTO {
    /** 名字 */
    private String name;
    /** 处理器id */
    private String processorId;
    /** 物理安装数量，实际上装了几个CPU */
    private Integer    physicalPackageCount;
    /** 物理核心数量 */
    private Integer    physicalProcessorCount;
    /** 逻辑核心数量 */
    private Integer    logicalProcessorCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhysicalPackageCount() {
        return physicalPackageCount;
    }

    public void setPhysicalPackageCount(int physicalPackageCount) {
        this.physicalPackageCount = physicalPackageCount;
    }

    public int getPhysicalProcessorCount() {
        return physicalProcessorCount;
    }

    public void setPhysicalProcessorCount(int physicalProcessorCount) {
        this.physicalProcessorCount = physicalProcessorCount;
    }

    public int getLogicalProcessorCount() {
        return logicalProcessorCount;
    }

    public void setLogicalProcessorCount(int logicalProcessorCount) {
        this.logicalProcessorCount = logicalProcessorCount;
    }

    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }

    @Override
    public String toString() {
        return "ProcessorDTO{" +
                "name='" + name + '\'' +
                ", processorId='" + processorId + '\'' +
                ", physicalPackageCount=" + physicalPackageCount +
                ", physicalProcessorCount=" + physicalProcessorCount +
                ", logicalProcessorCount=" + logicalProcessorCount +
                '}';
    }
}
