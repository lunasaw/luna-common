package com.luna.common.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * @version 1.0
 */
public class AsyncEngineUtils {

    private static final Logger          log            = LoggerFactory.getLogger(AsyncEngineUtils.class);

    private static final int             CORE_POOL_SIZE = 100;

    private static final int             MAX_POOL_SIZE  = 200;

    private static final ExecutorService executor;

    static {
        executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            60 * 5L,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 并发执行任务
     * 
     * @param tasks 任务
     * @return T 任务返回值
     */
    public static <T> List<T> concurrentExecute(Callable<T>... tasks) {
        if (tasks == null || tasks.length == 0) {
            return Lists.newArrayList();
        }
        return concurrentExecute(-1, null, tasks);
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

        return concurrentExecute(tasks.toArray(new Callable[tasks.size()]));
    }

    /**
     * 并发执行超时控制任务; 如果某任务超时或发生其他异常，则该任务返回值为null
     * 
     * @param timeout 超时时间
     * @param unit 超时时间单位
     * @param tasks 任务
     * @return T 任务返回值
     */
    public static <T> List<T> concurrentExecute(long timeout, TimeUnit unit, Callable<T>... tasks) {
        if (tasks == null || tasks.length == 0) {
            return Lists.newArrayList();
        }

        List<T> result = Lists.newArrayList();
        try {
            List<Future<T>> futures = timeout > 0 ? executor.invokeAll(Lists.newArrayList(tasks), timeout, unit)
                : executor.invokeAll(Lists.newArrayList(tasks));
            for (Future<T> future : futures) {
                T t = null;
                try {
                    t = future.get();
                } catch (CancellationException e) {
                    if (timeout > 0) {
                        log.error("concurrentExecute some task timeout!");
                    }
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
        executor.submit(task);
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
}