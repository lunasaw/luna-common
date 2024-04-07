package com.luna.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * thread 命名
 *
 * @author luna
 **/
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber   = new AtomicInteger(1);

    private final AtomicInteger        threadNumber = new AtomicInteger(1);
    private final ThreadGroup          group;
    private final String               namePrefix;
    private final boolean              isDaemon;

    public NamedThreadFactory(String name) {
        this(name, false);
    }

    public NamedThreadFactory(String prefix, boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
        isDaemon = daemon;
    }

    public Thread newThread(Runnable runnable) {
        Thread t = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
        // Default value is parent thread's
        t.setContextClassLoader(NamedThreadFactory.class.getClassLoader());
        t.setPriority(Thread.MAX_PRIORITY);
        t.setDaemon(isDaemon);
        return t;
    }
}