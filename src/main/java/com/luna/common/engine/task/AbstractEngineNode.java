package com.luna.common.engine.task;

import com.luna.common.engine.model.EngineContext;

/**
 * @author luna
 */
public abstract class AbstractEngineNode<T> implements EngineNode<T> {
    @Override
    public boolean couldContinueExecute(EngineContext engineContext) {
        if (engineContext.isStop()) {
            return false;
        }
        return true;
    }
}
