package com.luna.common.worker;

import java.util.List;
import java.util.concurrent.*;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.util.concurrent.RateLimiter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务worker
 * 
 * @version 1.0
 */
@Slf4j(topic = "sync")
public abstract class SyncTaskWorker<T> implements Runnable {
    /**
     * 任务队列
     */
    public final LinkedBlockingQueue<T> taskQueue;

    /**
     * 任务线程
     */
    public final ExecutorService        threadPool;

    /**
     * 处理速率
     */
    private final RateLimiter           rateLimiter;

    public SyncTaskWorker(LinkedBlockingQueue<T> taskQueue, ExecutorService threadPool, RateLimiter rateLimiter) {
        this.taskQueue = taskQueue;
        this.threadPool = threadPool;
        this.rateLimiter = rateLimiter;
    }

    public SyncTaskWorker(ExecutorService threadPool) {
        if (threadPool == null) {
            // 创建具有非公平访问策略的SynchronousQueue 。
            // 被拒绝任务的处理程序直接在execute方法的调用线程中运行被拒绝的任务，除非执行程序已关闭，在这种情况下任务将被丢弃。
            this.threadPool = new ThreadPoolExecutor(40, 40,
                60L, TimeUnit.MINUTES, new SynchronousQueue<>(), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        } else {
            this.threadPool = threadPool;
        }
        this.taskQueue = new LinkedBlockingQueue<>(100);
        this.rateLimiter = RateLimiter.create(30);
    }

    /**
     * initialize the task queue size
     */
    public abstract void init();

    public abstract List<T> getTaskList();

    public abstract void handleTask(T task);

    @SneakyThrows
    public void run() {
        init();

        if (taskQueue == null) {
            throw new Exception("queue is not initialized");
        }
        if (taskQueue.size() == Integer.MAX_VALUE) {
            throw new Exception("size of queue is illegal");
        }

        threadPool.execute(this::getTask);

        while (true) {
            T task = taskQueue.take();
            threadPool.execute(() -> {
                rateLimiter.acquire();
                handleTask(task);
            });
        }
    }

    @SneakyThrows
    public void getTask() {
        List<T> tasks;
        while (true) {
            tasks = getTaskList();
            log.info("getTaskList,tasks:{}", tasks);
            if (CollectionUtils.isEmpty(tasks)) {
                // sleep while list is empty
                Thread.sleep(1000);
            } else {
                // queue task
                for (T task : tasks) {
                    taskQueue.put(task);
                }
            }
        }
    }

}
