package com.luna.common.os.hardware;

import com.luna.common.date.DateUtils;

import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * JVM相关信息
 */
public class JvmDTO {

    /**
     * 当前JVM占用的内存总数(M)
     */
    private String total;

    /**
     * JVM最大可用内存总数(M)
     */
    private String max;

    /**
     * JVM空闲内存(M)
     */
    private String free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    private String runTime;

    private String startTime;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public void setStartTime(String startTime) {
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

    /**
     * JDK启动时间
     */
    public static String getStartTime() {
        return DateUtils.formatDateTime(DateUtils.getServerStartDate());
    }

    /**
     * JDK运行时间
     */
    public static String getRunTime() {
        return DateUtils.formatDateTime(
            new Date(DateUtils.getDaysBetween(DateUtils.getCurrentDate(), DateUtils.getServerStartDate())));
    }

}