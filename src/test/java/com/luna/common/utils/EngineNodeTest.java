package com.luna.common.utils;

import com.luna.common.spring.SpringBeanService;
import lombok.AllArgsConstructor;
import org.junit.Test;

import com.luna.common.engine.model.EngineContext;
import com.luna.common.engine.model.EngineRunData;
import com.luna.common.engine.model.NodeChain;
import com.luna.common.engine.model.NodeConf;
import com.luna.common.engine.task.AbstractEngineExecute;
import com.luna.common.engine.task.AbstractEngineNode;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2024/5/29
 */
@Slf4j
public class EngineNodeTest {

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
    }

    @Test
    public void atest() throws Exception {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(TestAdaptorConfig.class);
        annotationConfigApplicationContext.start();
        EngineRunData engineRunData = new EngineRunData();
        EngineContext engineContext = new EngineContext();
        TestEngineExecute testEngineExecute = new TestEngineExecute();
        TestAdaptor execute = testEngineExecute.execute(EngineFlow.testLink, engineRunData, engineContext);
        System.out.println(execute);
    }

    @Component
    public static class TestEngineNode extends AbstractEngineNode {

        @Override
        public Object invokeNode(EngineRunData nodeData, EngineContext engineContext) {
            log.info("invokeNode::nodeData = {}, engineContext = {}", nodeData, engineContext);
            return new TestAdaptor("hello");
        }

        @Override
        public void afterInvoke(EngineRunData nodeData, EngineContext engineContext) {
            log.info("afterInvoke::nodeData = {}, engineContext = {}", nodeData, engineContext);
        }

        @Override
        public String resultKey() {
            return "this_is_result_key";
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
            testLink.add(TestEngineNode.class, new NodeConf(true, 100));
        }
    }

    public class TestEngineExecute extends AbstractEngineExecute<TestAdaptor> {

        @Override
        public TestAdaptor assembleModel(EngineRunData engineRunData, EngineContext context) {
            return (TestAdaptor)context.getAdaptorMap().get("this_is_result_key");
        }
    }
}
