package com.luna.common.spring;

/**
 * Created by caiyichao on 17/8/29.
 */

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.luna.common.exception.BaseException;
import com.luna.common.reflect.ReflectUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring的bean加载服务类
 */
@Component
@Slf4j
public class SpringBeanService implements ApplicationContextAware {

    /**
     * spring bean上下文
     */
    protected static ApplicationContext applicationContext = null;
    private static Map<String/*** bean class name **/
        , Object>                       beanPool           = Maps.newConcurrentMap();

    /**
     * 获取bean实例
     */
    public static <T> T getBeanByName(String name, Class<T> clazz) throws BeansException {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 获取bean实例
     */
    public static Object getBeanByName(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 获取此类型所有的bean
     *
     * @param clazz
     * @return
     * @throws BeansException
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    /**
     * 根据class 类型获取bean
     *
     * @param clazz
     * @return
     * @throws BeansException 当有继承或者接口时(多个实现类)getBean(clazz)会报错
     * 所以通过class name比较来获取唯一那个bean
     */
    public static <T> T getSingleBeanByType(Class<T> clazz) throws Exception {
        // 由于后续的doGetSingleBeanByType()涉及到同步，高并发场景下会导致锁竞争，此处加缓存解决
        if (beanPool.get(clazz.getName()) != null) {
            return (T)beanPool.get(clazz.getName());
        }

        final T bean = doGetSingleBeanByType(clazz);
        beanPool.put(clazz.getName(), bean);
        return bean;
    }

    private static <T> T doGetSingleBeanByType(Class<T> clazz) throws Exception {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {
            Object beanByName = getBeanByName(beanName);
            Object target = ReflectUtils.getTarget(beanByName); // 防止被代理导致拿不到bean
            if (clazz.getName().equals(target.getClass().getName())) {
                return (T)beanByName;
            }
        }
        log.error("can not find bean by type, class = {}", clazz.getName());
        throw BaseException.SYSTEM_ERROR;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanService.applicationContext = applicationContext;
    }
}
