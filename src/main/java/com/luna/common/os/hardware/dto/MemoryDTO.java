package com.luna.common.os.hardware.dto;

/**
 * @author Tony
 */
public class MemoryDTO {
    /** 总物理内存 */
    private String memeryTotal;

    /** 总虚拟内存 */
    private String swapTotal;

    private String used;

    private String free;

    public String getMemeryTotal() {
        return memeryTotal;
    }

    public void setMemeryTotal(String memeryTotal) {
        this.memeryTotal = memeryTotal;
    }

    public String getSwapTotal() {
        return swapTotal;
    }

    public void setSwapTotal(String swapTotal) {
        this.swapTotal = swapTotal;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    @Override
    public String toString() {
        return "MemoryDTO{" +
            "memeryTotal=" + memeryTotal +
            ", swapTotal=" + swapTotal +
            ", used=" + used +
            ", free=" + free +
            '}';
    }
}
