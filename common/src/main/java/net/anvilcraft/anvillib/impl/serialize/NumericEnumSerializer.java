package net.anvilcraft.anvillib.impl.serialize;

import java.util.HashMap;
import java.util.Map;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.types.Result;

public class NumericEnumSerializer<T extends Enum<T>> implements IObjectSerializer<Integer, T> {

    private static Map<Class<? extends Enum<?>>, IObjectSerializer<Integer, ? extends Enum<?>>> instances = new HashMap<>();

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> IObjectSerializer<Integer, E> of(Class<E> clazz) {
        if (instances.containsKey(clazz)) return (IObjectSerializer<Integer, E>) instances.get(clazz);
        NumericEnumSerializer<E> instance = new NumericEnumSerializer<>();
        instance.clazz = clazz;
        instances.put(clazz, instance);
        return instance;
    }

    private NumericEnumSerializer() {

    }

    @Override
    public Result<Integer, Exception> serialize(T object) {
        return Result.ok(object.ordinal());
    }

    @Override
    public Result<T, Exception> deserialize(Integer encoded) {
        T[] values = clazz.getEnumConstants();
        if (!canDeserialize(encoded)) return Result.error(new IllegalArgumentException("Number not in range: " + encoded));
        return Result.ok(values[encoded]);
    }

    @Override
    public boolean canDeserialize(Integer encoded) {
        T[] values = clazz.getEnumConstants();
        return encoded >= 0 && encoded < values.length;
    }
    
}
