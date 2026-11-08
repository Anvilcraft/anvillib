package net.anvilcraft.anvillib.api.types;

import java.util.Map.Entry;

public class Pair<K, V> implements Entry<K, V> {

    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<K, V>(key, value);
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException("Pair is immutable");
    }
    
}
