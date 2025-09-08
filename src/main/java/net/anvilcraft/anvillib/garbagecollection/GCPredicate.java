package net.anvilcraft.anvillib.garbagecollection;

import javax.annotation.concurrent.ThreadSafe;

@FunctionalInterface
@ThreadSafe
public interface GCPredicate<K, V> {
    
    boolean isGarbage(K key, V value);

}
