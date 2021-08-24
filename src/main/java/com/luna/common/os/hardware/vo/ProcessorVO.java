package com.luna.common.os.hardware.vo;

/**
 * @author Tony
 */
public class ProcessorVO {
    /** 名字 */
    private String  name;
    /** 处理器id */
    private String  processorId;
    /** 物理安装数量，实际上装了几个CPU */
    private Integer physicalPackageCount;
    /** 物理核心数量 */
    private Integer physicalProcessorCount;
    /** 逻辑核心数量 */
    private Integer logicalProcessorCount;

    /**
     * 总计使用
     */
    private Double  nice;

    /**
     * 系统使用
     */
    private Double  system;

    /**
     * 用户使用
     */
    private Double  user;

    /**
     * IO等待
     */
    private Double  wait;

    /**
     * 闲
     */
    private Double  idle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }

    public Integer getPhysicalPackageCount() {
        return physicalPackageCount;
    }

    public void setPhysicalPackageCount(Integer physicalPackageCount) {
        this.physicalPackageCount = physicalPackageCount;
    }

    public Integer getPhysicalProcessorCount() {
        return physicalProcessorCount;
    }

    public void setPhysicalProcessorCount(Integer physicalProcessorCount) {
        this.physicalProcessorCount = physicalProcessorCount;
    }

    public Integer getLogicalProcessorCount() {
        return logicalProcessorCount;
    }

    public void setLogicalProcessorCount(Integer logicalProcessorCount) {
        this.logicalProcessorCount = logicalProcessorCount;
    }

    public Double getNice() {
        return nice;
    }

    public void setNice(Double nice) {
        this.nice = nice;
    }

    public Double getSystem() {
        return system;
    }

    public void setSystem(Double system) {
        this.system = system;
    }

    public Double getUser() {
        return user;
    }

    public void setUser(Double user) {
        this.user = user;
    }

    public Double getWait() {
        return wait;
    }

    public void setWait(Double wait) {
        this.wait = wait;
    }

    public Double getIdle() {
        return idle;
    }

    public void setIdle(Double idle) {
        this.idle = idle;
    }

    @Override
    public String toString() {
        return "ProcessorDTO{" +
            "name='" + name + '\'' +
            ", processorId='" + processorId + '\'' +
            ", physicalPackageCount=" + physicalPackageCount +
            ", physicalProcessorCount=" + physicalProcessorCount +
            ", logicalProcessorCount=" + logicalProcessorCount +
            ", nice='" + nice + '\'' +
            ", system='" + system + '\'' +
            ", user='" + user + '\'' +
            ", wait='" + wait + '\'' +
            ", idle='" + idle + '\'' +
            '}';
    }
}
