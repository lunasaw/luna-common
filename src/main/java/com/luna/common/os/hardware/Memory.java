package com.luna.common.os.hardware;

/**
 * @author Tony
 */
public class Memory {
    /** 总物理内存 */
    private Long memeryTotal;

    /** 总虚拟内存 */
    private Long swapTotal;

    private Long used;

    private Long free;

    public Long getMemeryTotal() {
        return memeryTotal;
    }

    public void setMemeryTotal(Long memeryTotal) {
        this.memeryTotal = memeryTotal;
    }

    public Long getSwapTotal() {
        return swapTotal;
    }

    public void setSwapTotal(Long swapTotal) {
        this.swapTotal = swapTotal;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
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
