package net.anvilcraft.anvillib.impl.serialize;

import java.util.HashMap;
import java.util.Map;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.types.Result;

public class StringEnumSerializer<T extends Enum<T>> implements IObjectSerializer<String, T> {

    private static Map<Class<? extends Enum<?>>, IObjectSerializer<String, ? extends Enum<?>>> instances = new HashMap<>();

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> IObjectSerializer<String, E> of(Class<E> clazz) {
        if (instances.containsKey(clazz)) return (IObjectSerializer<String, E>) instances.get(clazz);
        StringEnumSerializer<E> instance = new StringEnumSerializer<>();
        instance.clazz = clazz;
        instances.put(clazz, instance);
        return instance;
    }

    private StringEnumSerializer() {

    }

    @Override
    public Result<String, Exception> serialize(T object) {
        return Result.ok(object.name().toLowerCase());
    }

    @Override
    public Result<T, Exception> deserialize(String encoded) {
        return Result.create(encoded, (i) -> Enum.valueOf(clazz, encoded));
    }

    @Override
    public boolean canDeserialize(String encoded) {
        return true;
    }
    
}
