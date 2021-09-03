package com.luna.common.os.hardware;

import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * JVM相关信息
 */
public class Jvm {

    /**
     * 当前JVM占用的内存总数(M)
     */
    private Long   total;

    /**
     * JVM最大可用内存总数(M)
     */
    private Long   max;

    /**
     * JVM空闲内存(M)
     */
    private Long   free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    /** 运行时间 多少小时 */
    private Long   runTime;

    /** 开始时间 */
    private Date   startTime;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
        this.free = free;
    }

    public Long getRunTime() {
        return runTime;
    }

    public void setRunTime(Long runTime) {
        this.runTime = runTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取JDK名称
     */
    public String getName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    @Override
    public String toString() {
        return "JvmDTO{" +
            "total='" + total + '\'' +
            ", max='" + max + '\'' +
            ", free='" + free + '\'' +
            ", version='" + version + '\'' +
            ", home='" + home + '\'' +
            ", runTime='" + runTime + '\'' +
            ", startTime='" + startTime + '\'' +
            '}';
    }
}