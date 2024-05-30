package com.luna.common.utils;

import org.junit.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.luna.common.engine.model.EngineContext;
import com.luna.common.engine.model.EngineRunData;
import com.luna.common.engine.model.NodeChain;
import com.luna.common.engine.model.NodeConf;
import com.luna.common.engine.spi.AbstractBatchNodeNodeSpi;
import com.luna.common.engine.task.AbstractEngineExecute;
import com.luna.common.engine.task.AbstractEngineNode;
import com.luna.common.spring.SpringBeanService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2024/5/29
 */
@Slf4j
public class EngineNodeTest {

    private static final String RESULT_KEY = "this_is_result_key";

    @Test
    public void atest() {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(TestAdaptorConfig.class);
        annotationConfigApplicationContext.start();
        EngineRunData engineRunData = new EngineRunData();
        EngineContext engineContext = new EngineContext();
        TestEngineExecute testEngineExecute = new TestEngineExecute();
        TestAdaptor execute = testEngineExecute.execute(EngineFlow.testLink, engineRunData, engineContext);
        System.out.println(execute);
    }

    @Configuration
    public static class TestAdaptorConfig {
        @Bean
        public TestEngineNode testEngineNode() {
            return new TestEngineNode();
        }

        @Bean
        public SpringBeanService springBeanService() {
            return new SpringBeanService();
        }

        @Bean
        public TestBatchNodeNodeSpi testBatchNodeNodeSpi() {
            return new TestBatchNodeNodeSpi();
        }
    }

    @Component
    public static class TestBatchNodeNodeSpi extends AbstractBatchNodeNodeSpi<TestAdaptor> implements InitializingBean {

        @Autowired
        private TestEngineNode testEngineNode;

        @Override
        public void invoke(EngineRunData engineRunData) {
            System.out.println(JSON.toJSONString(engineRunData));
        }

        @Override
        public void afterPropertiesSet() {
            testEngineNode.add(this);
        }
    }

    @Component
    public static class TestEngineNode extends AbstractEngineNode<TestAdaptor> {

        @Override
        public TestAdaptor invokeNode(EngineRunData nodeData, EngineContext engineContext) {
            log.info("invokeNode::nodeData = {}, engineContext = {}", nodeData, engineContext);
            return new TestAdaptor("hello");
        }

        @Override
        public void afterInvoke(EngineRunData nodeData, EngineContext engineContext) {
            log.info("afterInvoke::nodeData = {}, engineContext = {}", nodeData, engineContext);
        }

        @Override
        public String resultKey() {
            return RESULT_KEY;
        }
    }

    @Data
    @AllArgsConstructor
    public static class TestAdaptor {
        private String hello;
    }

    @Data
    public static class EngineFlow {
        private static NodeChain testLink = new NodeChain();

        static {
            testLink.add("1", TestEngineNode.class, new NodeConf(true, 100));
            testLink.add("1", TestEngineNode.class, new NodeConf(true, 100));
            testLink.add("2", TestEngineNode.class, new NodeConf(true, 100));
            testLink.add("2", TestEngineNode.class, new NodeConf(true, 100));
        }
    }

    public static class TestEngineExecute extends AbstractEngineExecute<TestAdaptor> {

        @Override
        public TestAdaptor assembleModel(EngineRunData engineRunData, EngineContext context) {
            return (TestAdaptor)context.getAdaptorMap().get(RESULT_KEY);
        }
    }
}
