package com.luna.common.thread;

import java.util.List;
import java.util.concurrent.*;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * @version 1.0
 */
public class AsyncEngineUtils {

    private static final Logger          log             = LoggerFactory.getLogger(AsyncEngineUtils.class);

    private static final int             CORE_POOL_SIZE  = 200;

    private static final int             MAX_POOL_SIZE   = 200;

    private static final int             KEEP_ALIVE_TIME = 60 * 5;

    private static final int             QUEUE_CAPACITY  = 1000;

    private static final long            TIME_OUT        = 300;

    private static final int             MONITOR_PERIOD  = 5;                                                                                               // 监控时间间隔，单位：s

    private static final ExecutorService EXECUTOR;

    private static final Runnable        MONITOR_TASK    = new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 try {
                                                                     ThreadPoolExecutor threadPool = (ThreadPoolExecutor)EXECUTOR;
                                                                     int activeCount = threadPool.getActiveCount();                                         // 正在执行的任务数
                                                                     long completedTaskCount = threadPool.getCompletedTaskCount();                          // 已完成任务数
                                                                     long totalTaskCount = threadPool.getTaskCount();                                       // 总任务数
                                                                     int queueSize = threadPool.getQueue().size();
                                                                     int coreSize = threadPool.getCorePoolSize();

                                                                     log.info(
                                                                         "total_task:{}, active_thread:{}, queue_size:{}, completed_thread:{}, coreSize:{}",
                                                                         totalTaskCount, activeCount, queueSize, completedTaskCount, coreSize);

                                                                 } catch (Exception e) {
                                                                     log.error("[SYSTEM-SafeGuard]Monitor thread run fail", e);
                                                                 }
                                                             }
                                                         };

    static {
        EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(QUEUE_CAPACITY),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("AsyncEngine-Monitor", true));
        monitor.scheduleAtFixedRate(MONITOR_TASK, MONITOR_PERIOD, MONITOR_PERIOD, TimeUnit.SECONDS);

    }

    /**
     * 并发执行任务
     * 
     * @param tasks 任务
     * @return T 任务返回值
     */
    @SafeVarargs
    public static <T> List<T> concurrentExecute(Callable<T>... tasks) {
        if (tasks == null || tasks.length == 0) {
            return Lists.newArrayList();
        }
        return concurrentExecute(-1, null, Lists.newArrayList(tasks));
    }

    /**
     * 并发执行具有同样返回值的任务
     *
     * @param tasks 任务
     * @return T 任务返回值
     */
    public static <T> List<T> concurrentExecute(List<Callable<T>> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            return Lists.newArrayList();
        }

        return concurrentExecute(-1, null, tasks);
    }

    /**
     * 并发执行超时控制任务; 如果某任务超时或发生其他异常，则该任务返回值为null
     * 
     * @param timeout 超时时间
     * @param unit 超时时间单位
     * @param tasks 任务
     * @return T 任务返回值
     */
    public static <T> List<T> concurrentExecute(long timeout, TimeUnit unit, List<Callable<T>> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            return Lists.newArrayList();
        }

        List<T> result = Lists.newArrayList();
        try {
            List<Future<T>> futures = timeout > 0 ? EXECUTOR.invokeAll(tasks, timeout, unit)
                : EXECUTOR.invokeAll(tasks);
            for (Future<T> future : futures) {
                T t = null;
                try {
                    t = future.get(TIME_OUT, TimeUnit.MILLISECONDS);
                } catch (CancellationException e) {
                    if (timeout > 0) {
                        log.error("concurrentExecute some task timeout!");
                    }
                } catch (TimeoutException tt) {
                    log.error("future.get() TimeoutException ", tt);
                } catch (Throwable tt) {
                    log.error("future.get() Exception ", tt);
                }
                result.add(t);
            }
        } catch (InterruptedException e) {
            log.error("executor.invokeAll() Exception", e);
        }
        return result;
    }

    /**
     * 异步执行任务，不需要返回值
     *
     * @param task
     */
    public static void execute(Runnable task) {
        if (task == null) {
            return;
        }
        EXECUTOR.submit(task);
    }

    public static void main(String[] args) {
        List<Callable<Void>> list = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            list.add(() -> {
                System.out.println("hello");
                return null;
            });
        }
        List<Void> voids = concurrentExecute(list);
        System.out.println(voids);
    }

    public void destroy() {
        log.warn("start to stop thread pool");
        EXECUTOR.shutdown();
        log.warn("finish to stop thread pool");
    }
}