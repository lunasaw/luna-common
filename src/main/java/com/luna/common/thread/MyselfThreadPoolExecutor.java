package com.luna.common.thread;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyselfThreadPoolExecutor extends ThreadPoolExecutor {

    /** 初始化父类构造函数及startTime */
    public MyselfThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
        long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {

        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /** 按过去执行已提交任务的顺序发起一个有序的关闭，但是不接受新任务(已执行的任务不会停止) */
    @Override
    public void shutdown() {

        super.shutdown();

    }

    /** 尝试停止所有的活动执行任务、暂停等待任务的处理，并返回等待执行的任务列表。在从此方法返回的任务队列中排空（移除）这些任务。并不保证能够停止正在处理的活动执行任务，但是会尽力尝试。 */
    @Override
    public List<Runnable> shutdownNow() {

        return super.shutdownNow();

    }

    /** 在执行给定线程中的给定 Runnable 之前调用的方法.可用于重新初始化ThreadLocals或者执行日志记录。 */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {

        super.beforeExecute(t, r);
    }

    /** 基于完成执行给定 Runnable 所调用的方法 */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    /**
     * 
     * getQueueSize:(已执行的任务数). <br/>
     *
     * @author
     * @return
     */
    @Override
    public long getCompletedTaskCount() {

        return super.getCompletedTaskCount();
    }

    /**
     * 
     * getQueueSize:(正在运行的任务数). <br/>
     *
     * @author
     * @return
     */
    @Override
    public int getActiveCount() {

        return super.getActiveCount();
    }

    /**
     * 
     * getQueueSize:(队列等待任务数). <br/>
     *
     * @author
     * @return
     */
    public int getQueueSize() {

        return getQueue().size();
    }
}