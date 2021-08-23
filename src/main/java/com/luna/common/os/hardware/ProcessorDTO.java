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

    /**
     * 总计使用
     */
    private String  nice;

    /**
     * 系统使用
     */
    private String  system;

    /**
     * 用户使用
     */
    private String  user;

    /**
     * IO等待
     */
    private String  wait;

    /**
     * 闲
     */
    private String  idle;

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

    public String getNice() {
        return nice;
    }

    public void setNice(String nice) {
        this.nice = nice;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getWait() {
        return wait;
    }

    public void setWait(String wait) {
        this.wait = wait;
    }

    public String getIdle() {
        return idle;
    }

    public void setIdle(String idle) {
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
