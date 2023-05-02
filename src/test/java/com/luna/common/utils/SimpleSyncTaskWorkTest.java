package com.luna.common.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;

import com.luna.common.worker.SyncTaskWorker;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author weidian
 * @description
 * @date 2023/4/30
 */
@Slf4j
public class SimpleSyncTaskWorkTest extends SyncTaskWorker<Integer> {
    protected SimpleSyncTaskWorkTest() {
        super(null);
    }

    public static void main(String[] args) {
        new SimpleSyncTaskWorkTest().run();
    }

    @Override
    public void init() {

    }

    @Override
    public List<Integer> getTaskList() {
        return IntStream.range(0, 1).map(i -> RandomUtils.nextInt(0, 100)).boxed().collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void handleTask(Integer task) {
        log.info("handleTask::task = {}", task);
    }
}
