package com.luna.common.os.hardware;

/**
 * @author Tony
 */
public class MemoryDTO {
    /** 总物理内存 */
    private long memeryTotal;

    /** 总虚拟内存 */
    private long swapTotal;

    public long getMemeryTotal() {
        return memeryTotal;
    }

    public void setMemeryTotal(long memeryTotal) {
        this.memeryTotal = memeryTotal;
    }

    public long getSwapTotal() {
        return swapTotal;
    }

    public void setSwapTotal(long swapTotal) {
        this.swapTotal = swapTotal;
    }

    @Override
    public String toString() {
        return "MemoryDTO{" +
                "memeryTotal=" + memeryTotal +
                ", swapTotal=" + swapTotal +
                '}';
    }
}
