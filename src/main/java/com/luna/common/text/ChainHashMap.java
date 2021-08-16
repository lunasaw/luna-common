package com.luna.common.text;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author luna
 */
public class ChainHashMap<K, V> extends HashMap<K, V> {

    public ChainHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ChainHashMap() {
        super();
    }

    public ChainHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public ChainHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public static <K extends Comparable, V> ChainHashMap<K, V> newChainMap() {
        return new ChainHashMap<>();
    }

    /**
     * 对 HashMap 的 put() 的方法进行封转返回 ChainHashMap 来实现 链式添加
     * 
     * @param key k
     * @param value v
     * @return
     */
    public ChainHashMap<K, V> putIfNull(K key, V value) {
        if (ObjectUtils.isNotEmpty(key) && ObjectUtils.isNotEmpty(value)) {
            this.put(key, value);
        }
        return this;
    }
}