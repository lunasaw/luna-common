package com.luna.common.engine.task;

import com.luna.common.engine.model.EngineContext;
import com.luna.common.engine.spi.NodeSpi;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luna
 */
@Data
public abstract class AbstractEngineNode<T> implements EngineNode<T> {
    /**
     * SPI列表
     */
    private List<NodeSpi> spiList = new ArrayList<>();

    @Override
    public boolean couldContinueExecute(EngineContext engineContext) {
        if (engineContext.isStop()) {
            return false;
        }
        return true;
    }
}
