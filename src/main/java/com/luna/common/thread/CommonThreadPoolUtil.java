package com.luna.common.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.luna.common.dto.ResultDTO;
import com.luna.common.dto.ResultDTOUtils;
import com.luna.common.dto.constant.ResultCode;

import lombok.Data;

/**
 * ClassName:CommenThreadPoolUtil <br/>
 * Function:线程池公共入口处理类. <br/>
 *
 */
@Data
public class CommonThreadPoolUtil {

    private static final long KEEP_ALIVE_TIME       = 0L;
    private static final Logger                log         = LoggerFactory.getLogger(CommonThreadPoolUtil.class);
    /** 核心线程数(默认初始化为10) */
    private static volatile int                cacheCorePoolSize     = 5;
    /** 核心线程控制的最大数目 */
    private static volatile int                maxCorePoolSize       = 50;
    /** 队列等待线程数阈值 */
    private static volatile int                blockingQueueWaitSize = 2000;
    /** 核心线程数自动调整的增量幅度 */
    private static volatile int                incrementCorePoolSize = 4;
    /** 初始化线程池 */
    private static ThreadPoolExecutor          threadPool            =
        new ThreadPoolExecutor(cacheCorePoolSize, cacheCorePoolSize, KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    /** 初始化线程对象ThreadLocal,重写initialValue()，保证ThreadLocal首次执行get方法时不会null异常 */
    private final ThreadLocal<List<Future<?>>> threadLocal = ThreadLocal.withInitial(ArrayList::new);

    public synchronized static void refresh() {
        threadPool = new ThreadPoolExecutor(cacheCorePoolSize, maxCorePoolSize, KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    }

    /**
     *
     * dealTask:(线程池执行操作-包含每个进程返回结果). <br/>
     * 1、运用场景：例如，需要同时校验很多不同的逻辑，依赖于获取校验结果响应给用户； 2、具体实现java类：implements
     * 的Callable接口，重写call方法即可，支持返回值
     *
     * @author
     * @param callable
     * @return
     */
    public ResultDTO<Void> dealTask(Callable<?> callable) {
        try {
            // 动态更改核心线程数大小
            dynamicTuningPoolSize();
            // 执行线程业务逻辑及获取返回结果
            Future<?> result = threadPool.submit(callable);
            // 获取当前进程的局部变量
            List<Future<?>> threadLocalResult = threadLocal.get();
            // 叠加主进程对应的多个进程处理结果
            threadLocalResult.add(result);
            // 设置最新的threadLocal变量到当前主进程
            threadLocal.set(threadLocalResult);
        } catch (Exception e) {
            log.error("dealTask::callable = {} ", callable, e);
            return ResultDTOUtils.failure(ResultCode.ERROR_SYSTEM_EXCEPTION, "线程池发生异常-Future");
        }
        return ResultDTOUtils.success();
    }

    /**
     *
     * dealTask:(线程池执行操作-不包含每个进程返回结果). <br/>
     * 1、运用场景：例如，不依赖于响应给用户执行结果的业务逻辑 ； 2、具体实现java类：implements
     * 的Runnable接口，重写run方法，没有返回值
     *
     * @author
     * @param runnable
     * @return
     */
    public ResultDTO<Void> dealTask(Runnable runnable) {

        try {
            // 动态更改核心线程数大小
            dynamicTuningPoolSize();
            // 执行线程业务逻辑
            threadPool.execute(runnable);
        } catch (Exception e) {
            log.error("dealTask::runnable = {} ", runnable, e);
            return ResultDTOUtils.failure(ResultCode.ERROR_SYSTEM_EXCEPTION, "线程池发生异常");
        }
        return ResultDTOUtils.success();
    }

    /**
     * obtainTaskFuture:(获取线程池执行结果：此为阻塞线程，即所有线程都执行完成才能获取结果，故应将执行时间稍长的业务逻辑先执行，
     * 减少等待时间). <br/>
     * 此方法只能调用一次，即调用之后清除ThreadLocal变量，以便于同一进程再次调用线程池获取最新的执行结果以及释放内存， 防止内存泄露
     *
     * @author
     * @return
     */
    public ResultDTO<Object> obtainTaskFuture() {

        List<Future<?>> threadLocalResult = null;
        try {
            // 获取当前进程变量
            threadLocalResult = threadLocal.get();
            if (CollectionUtils.isEmpty(threadLocalResult)) {
                return ResultDTOUtils.failure(ResultCode.PARAMETER_INVALID, "获取线程池执行结果为空", null);
            }
            return ResultDTOUtils.success(threadLocalResult);
        } catch (Exception e) {
            return ResultDTOUtils.failure(ResultCode.ERROR_SYSTEM_EXCEPTION, "获取线程池执行结果发生异常:" + e.getMessage(), null);
        } finally {
            // 1、释放内存；2、防止主进程再次调用线程池方法时对结果互有影响。
            threadLocal.remove();
        }

    }

    /**
     *
     * dynamicTuningPoolSize:(动态改变核心线程数). <br/>
     *
     * @author
     * @return
     */
    private void dynamicTuningPoolSize() {

        // 队列等待任务数（此为近似值，故采用>=判断）
        int queueSize = threadPool.getQueue().size();
        // 动态更改核心线程数大小
        if (queueSize >= blockingQueueWaitSize) {
            // 核心线程数小于设定的最大线程数才会自动扩展线程数
            if (cacheCorePoolSize <= maxCorePoolSize) {
                // 原有核心线程数
                int corePoolSize = threadPool.getCorePoolSize();
                // 将要累积的核心线程数
                int currentcorePoolSize = corePoolSize + incrementCorePoolSize;
                threadPool.setCorePoolSize(currentcorePoolSize);
                threadPool.setMaximumPoolSize(currentcorePoolSize);
                cacheCorePoolSize = currentcorePoolSize;
                log.warn("动态改变线程池大小====原核心线程池数目为：" + corePoolSize + ";现累加为：" + currentcorePoolSize);
            } else {
                log.warn("动态改变线程池大小====核心线程池数目已累加为：" + cacheCorePoolSize + "；不会继续无限增加");
            }
        }
    }

    /**
     * 获取核心线程数 getCacheCorePoolSize:(). <br/>
     *
     * @author
     * @return
     */
    public int getCacheCorePoolSize() {
        return threadPool.getCorePoolSize();
    }

    /**
     * 设置核心线程数 setCacheCorePoolSize:(). <br/>
     *
     * @author
     * @param cacheCorePoolSize
     */
    public void setCacheCorePoolSize(int cacheCorePoolSize) {

        threadPool.setCorePoolSize(cacheCorePoolSize);
        threadPool.setMaximumPoolSize(cacheCorePoolSize);
        CommonThreadPoolUtil.cacheCorePoolSize = cacheCorePoolSize;
    }

    public static void main(String[] args) {
        CommonThreadPoolUtil commonThreadPoolUtil = new CommonThreadPoolUtil();
        commonThreadPoolUtil.setCacheCorePoolSize(20);
        for (int i = 0; i < 100; i++) {
            commonThreadPoolUtil.dealTask((Callable<String>)() -> {
                System.out.println("线程池执行任务");
                return "线程池执行任务";
            });
        }
        ResultDTO<Object> objectResultDTO = commonThreadPoolUtil.obtainTaskFuture();
        System.out.println(objectResultDTO);
    }
}
