package com.luna.common.map;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author luna
 */
public class ChainTreeMap<K, V> extends TreeMap<K, V> {

    public ChainTreeMap() {
        super();
    }

    public ChainTreeMap(Comparator<? super K> comparator) {
        super(comparator);
    }

    public ChainTreeMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public ChainTreeMap(SortedMap<K, ? extends V> m) {
        super(m);
    }

    public static <K extends Comparable, V> ChainTreeMap<K, V> newChainMap() {
        return new ChainTreeMap<>();
    }

    /**
     * 对 HashMap 的 put() 的方法进行封转返回 ChainHashMap 来实现 链式添加
     * 
     * @param key k
     * @param value v
     * @return
     */
    public ChainTreeMap<K, V> putIfNotEmpty(K key, V value) {
        if (ObjectUtils.isNotEmpty(key) && ObjectUtils.isNotEmpty(value)) {
            this.put(key, value);
        }
        return this;
    }
}