package com.luna.common.engine.task;

import java.util.concurrent.Callable;

/**
 *
 * 交易engine线程执行的公共父类
 *
 */
public abstract class TradeEngineCallable<V> implements Callable<V> {

    @Override
    public V call() throws Exception {
        return _call();
    }

    abstract V _call() throws Exception;

}
