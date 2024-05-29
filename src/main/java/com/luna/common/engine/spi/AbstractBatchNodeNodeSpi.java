package com.luna.common.engine.spi;

import com.luna.common.engine.model.EngineContext;

public abstract class AbstractBatchNodeNodeSpi<T> implements BatchNodeNodeSpi<T> {

    @Override
    public boolean isAccept(T spiData, EngineContext context) {
        return true;
    }

}
