package com.luna.common.utils;

import com.google.common.cache.CacheLoader;
import com.luna.common.cache.SimpleGuavaCache;

public class GuavaCacheTest {

    private static final SimpleGuavaCache<String, String> cache = new SimpleGuavaCache<>(new CacheLoader<String, String>() {
        public String load(String key) throws Exception {
            System.out.println("load data"); // 加载数据线程执行标志
            Thread.sleep(1000); // 模拟加载时间
            return "auto load by CacheLoader";
        }
    });

    public static void main(String[] args) {

        new Thread(() -> {
            System.out.println("thread1");
            String value = cache.get("key", () -> {
                System.out.println("load1"); // 加载数据线程执行标志
                Thread.sleep(1000); // 模拟加载时间
                return "auto load by Callable";
            });
            System.out.println("thread1 " + value);
        }).start();

        new Thread(() -> {
            System.out.println("thread2");
            String value = cache.get("key", () -> {
                System.out.println("load2"); // 加载数据线程执行标志
                Thread.sleep(1000); // 模拟加载时间
                return "auto load by Callable";
            });
            System.out.println("thread2 " + value);
        }).start();
    }
}