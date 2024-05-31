package com.luna.common.engine.spi;

import com.luna.common.engine.model.EngineRunData;

public abstract class AbstractBatchNodeNodeSpi<T> implements BatchNodeNodeSpi<T> {

    @Override
    public boolean isAccept(EngineRunData engineRunData) {
        return true;
    }

}
