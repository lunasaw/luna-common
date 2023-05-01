package com.luna.common.cache;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.*;
import com.google.common.collect.ImmutableMap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
public class SimpleGuavaCache<K, V> {

    private LoadingCache<K, V>    cache;

    private RemovalListener<K, V> listener;

    /** LRU缓存的最大个数 */
    private Long                  maximumSize = 200L;

    public SimpleGuavaCache(CacheLoader<K, V> loader) {
        this(loader, new DefaultRemovalListener<>());
    }

    public SimpleGuavaCache(CacheLoader<K, V> loader, RemovalListener<K, V> listener) {
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(maximumSize)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .removalListener(listener)
            .build(loader);
    }

    @SneakyThrows
    public V get(K key) {
        return cache.get(key);
    }

    @SneakyThrows
    public V get(K key, Callable<? extends V> loader) {
        return cache.get(key, loader);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void invalidate(K... key) {
        Arrays.stream(key).forEach(cache::invalidate);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

    public void cleanUp() {
        cache.cleanUp();
    }

    public Long size() {
        return cache.size();
    }

    public CacheStats stats() {
        return cache.stats();
    }

    public ConcurrentMap<K, V> asMap() {
        return cache.asMap();
    }

    /**
     * 为key加载一个新值，可能是异步的。当新值加载时，以前的值（如果有的话）将继续由get(key)返回，除非它被驱逐。如果新值加载成功，
     * 它将替换缓存中的先前值；如果在刷新时抛出异常，则先前的值将保留，并且异常将被记录（使用java.util.logging.Logger ）并被吞掉。
     * 如果缓存当前包含key的值，则由CacheLoader加载的缓存将调用CacheLoader.reload ，
     * 否则调用CacheLoader.load 。仅当CacheLoader.reload被异步实现覆盖时，加载才是异步的。
     * 
     * @param key
     */
    public void refresh(K key) {
        cache.refresh(key);
    }

    public ImmutableMap<K, V> getAllPresent(K... keys) {
        return cache.getAllPresent(Arrays.stream(keys).distinct().collect(Collectors.toList()));
    }

    @SneakyThrows
    public ImmutableMap<K, V> method(List<K> keys) {
        return cache.getAll(keys);
    }

    public V getUnchecked(K key) {
        return cache.getUnchecked(key);
    }

    @Slf4j
    static class DefaultRemovalListener<K, V> implements RemovalListener<K, V> {

        @Override
        public void onRemoval(RemovalNotification<K, V> notification) {
            log.info("onRemoval::notification = {}", JSON.toJSONString(notification));
        }
    }
}
