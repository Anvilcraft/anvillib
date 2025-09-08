package net.anvilcraft.anvillib.garbagecollection;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class GCManager {

    public static GCManager INSTANCE = new GCManager();
    private Set<GCEntry> maps = new HashSet<>();
    private GarbageCollector<GCEntry> collector = null;
    
    public <K, V> Map<K, V> createGarbageCollectedMap(GCPredicate<K, V> predicate) {
        return createGarbageCollectedMap(predicate, (m) -> {});
    }

    public <K, V> Map<K, V> createGarbageCollectedMap(GCPredicate<K, V> predicate, Consumer<Map<K, V>> worldUnloadHook) {
        Map<K, V> map = new ConcurrentHashMap<>();
        maps.add(new GCEntry<>(map, predicate, worldUnloadHook));
        if (collector == null) {
            collector = new GarbageCollector<GCEntry>(maps, GCManager::processMap);
            collector.start();
        }
        return map;
    }

    private static class GCEntry<K, V> {
        Map<K, V> map;
        GCPredicate<K, V> predicate;
        Consumer<Map<K, V>> hook;

        public GCEntry(Map<K, V> map, GCPredicate<K, V> predicate, Consumer<Map<K, V>> hook) {
            this.map = map;
            this.predicate = predicate;
            this.hook = hook;
        }
    }

    private static <K, V> void processMap(GCEntry<K, V> entry) {
        Set<K> toCollect = entry.map.entrySet().stream().filter((e) -> entry.predicate.isGarbage(e.getKey(), e.getValue())).map((e) -> e.getKey()).collect(Collectors.toSet());
        toCollect.forEach((k) -> entry.map.remove(k));
    }

    @SuppressWarnings("unchecked")
    public void unloadWorld() {
        for (GCEntry entry : maps) {
            entry.hook.accept(entry.map);
        }
    }

}
