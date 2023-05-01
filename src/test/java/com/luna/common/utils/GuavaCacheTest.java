package com.luna.common.utils;

import java.util.concurrent.Callable;

import com.google.common.cache.CacheLoader;
import com.luna.common.cache.SimpleGuavaCache;

public class GuavaCacheTest {

    private static final SimpleGuavaCache<String, String> cache = new SimpleGuavaCache<String, String>(new CacheLoader<String, String>() {
        public String load(String key) throws Exception {
            System.out.println("load data"); // 加载数据线程执行标志
            Thread.sleep(1000); // 模拟加载时间
            return "auto load by CacheLoader";
        }
    });

    public static void main(String[] args) throws InterruptedException {

        new Thread(new Runnable() {
            public void run() {
                System.out.println("thread1");
                String value = cache.get("key", new Callable<String>() {
                    public String call() throws Exception {
                        System.out.println("load1"); // 加载数据线程执行标志
                        Thread.sleep(1000); // 模拟加载时间
                        return "auto load by Callable";
                    }
                });
                System.out.println("thread1 " + value);
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                System.out.println("thread2");
                String value = cache.get("key", new Callable<String>() {
                    public String call() throws Exception {
                        System.out.println("load2"); // 加载数据线程执行标志
                        Thread.sleep(1000); // 模拟加载时间
                        return "auto load by Callable";
                    }
                });
                System.out.println("thread2 " + value);
            }
        }).start();
    }
}