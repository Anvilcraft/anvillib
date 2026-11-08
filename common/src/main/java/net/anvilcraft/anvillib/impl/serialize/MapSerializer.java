package net.anvilcraft.anvillib.impl.serialize;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.serialize.IObjectType;
import net.anvilcraft.anvillib.api.types.Result;

public class MapSerializer<T, K, V> implements IObjectSerializer<T, Map<K, V>> {

    private IObjectType<T> manipulator;
    private IObjectSerializer<String, K> keySerializer;
    private IObjectSerializer<T, V> valuSerializer;

    public MapSerializer(IObjectType<T> manipulator, IObjectSerializer<String, K> keySerializer,
            IObjectSerializer<T, V> valuSerializer) {
        this.manipulator = manipulator;
        this.keySerializer = keySerializer;
        this.valuSerializer = valuSerializer;
    }

    @Override
    public Result<T, Exception> serialize(Map<K, V> object) {
        return Result.create(object, (i) -> {
            T obj = manipulator.objects().create();
            for (Entry<K, V> e : i.entrySet()) {
                manipulator.objects().set(obj, keySerializer.serialize(e.getKey()).getOrThrow(), valuSerializer.serialize(e.getValue()).getOrThrow());
            }
            return obj;
        });
        
    }

    @Override
    public Result<Map<K, V>, Exception> deserialize(T encoded) {
        return Result.create(encoded, (i) -> {
            Map<K, V> map = new HashMap<>();
            for (String key : manipulator.objects().getIndices(i)) {
                T value = manipulator.objects().get(i, key);
                map.put(keySerializer.deserialize(key).getOrThrow(), valuSerializer.deserialize(value).getOrThrow());
            }
            return map;
        });
    }

    @Override
    public boolean canDeserialize(T encoded) {
        return true;
    }
    
}
