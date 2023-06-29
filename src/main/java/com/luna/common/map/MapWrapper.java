package com.luna.common.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.luna.common.text.SerializeUtil;

/**
 * Map包装类，通过包装一个已有Map实现特定功能。例如自定义Key的规则或Value规则
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author looly
 * @since 4.3.3
 */
public class MapWrapper<K, V> implements Map<K, V>, Iterable<Map.Entry<K, V>>, Serializable, Cloneable {
    /**
     * 默认增长因子
     */
    protected static final float DEFAULT_LOAD_FACTOR      = 0.75f;
    /**
     * 默认初始大小
     */
    protected static final int   DEFAULT_INITIAL_CAPACITY = 1 << 4;               // aka 16
    private static final long    serialVersionUID         = -7524578042008586382L;
    private Map<K, V>            raw;

    /**
     * 构造<br>
     * 通过传入一个Map从而确定Map的类型，子类需创建一个空的Map，而非传入一个已有Map，否则值可能会被修改
     *
     * @param mapFactory 空Map创建工厂
     * @since 5.8.0
     */
    public MapWrapper(Supplier<Map<K, V>> mapFactory) {
        this(mapFactory.get());
    }

    /**
     * 构造
     *
     * @param raw 被包装的Map
     */
    public MapWrapper(Map<K, V> raw) {
        this.raw = raw;
    }

    /**
     * Map的键和值互换<br>
     * 互换键值对不检查值是否有重复，如果有则后加入的元素替换先加入的元素<br>
     * 值的顺序在HashMap中不确定，所以谁覆盖谁也不确定，在有序的Map中按照先后顺序覆盖，保留最后的值
     *
     * @param <K> 键和值类型
     * @param <V> 键和值类型
     * @param map Map对象，键值类型必须一致
     * @return 互换后的Map
     * @since 5.2.6
     */
    public static <K, V> Map<V, K> inverse(Map<K, V> map) {
        final Map<V, K> result = new HashMap<>();
        map.forEach((key, value) -> result.put(value, key));
        return result;
    }

    /**
     * 获取原始的Map
     *
     * @return Map
     */
    public Map<K, V> getRaw() {
        return this.raw;
    }

    @Override
    public int size() {
        return raw.size();
    }

    @Override
    public boolean isEmpty() {
        return raw.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return raw.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return raw.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return raw.get(key);
    }

    @Override
    public V put(K key, V value) {
        return raw.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return raw.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        raw.putAll(m);
    }

    @Override
    public void clear() {
        raw.clear();
    }

    @Override
    public Collection<V> values() {
        return raw.values();
    }

    @Override
    public Set<K> keySet() {
        return raw.keySet();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return raw.entrySet();
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return this.entrySet().iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapWrapper<?, ?> that = (MapWrapper<?, ?>)o;
        return Objects.equals(raw, that.raw);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raw);
    }

    @Override
    public String toString() {
        return raw.toString();
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        raw.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        raw.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return raw.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return raw.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return raw.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return raw.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return raw.computeIfAbsent(key, mappingFunction);
    }

    // 重写默认方法的意义在于，如果被包装的Map自定义了这些默认方法，包装类就可以保持这些行为的一致性
    // ---------------------------------------------------------------------------- Override default methods start
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return raw.getOrDefault(key, defaultValue);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return raw.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return raw.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return raw.merge(key, value, remappingFunction);
    }

    // ---------------------------------------------------------------------------- Override default methods end

    @Override
    public MapWrapper<K, V> clone() throws CloneNotSupportedException {
        @SuppressWarnings("unchecked")
        final MapWrapper<K, V> clone = (MapWrapper<K, V>)super.clone();
        try {
            clone.raw = SerializeUtil.clone(raw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    // region 序列化与反序列化重写
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.raw);
    }
    // endregion

    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        raw = (Map<K, V>)in.readObject();
    }
}
