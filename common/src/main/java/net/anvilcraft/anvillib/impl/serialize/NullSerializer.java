package net.anvilcraft.anvillib.impl.serialize;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.types.Result;

public class NullSerializer<T> implements IObjectSerializer<T, T> {

    private static NullSerializer<?> instance = new NullSerializer<>();

    @SuppressWarnings("unchecked")
    public static <T> IObjectSerializer<T, T> instance() {
        return (IObjectSerializer<T, T>) instance;
    }

    @Override
    public Result<T, Exception> serialize(T object) {
        return Result.ok(object);
    }

    @Override
    public Result<T, Exception> deserialize(T encoded) throws IllegalArgumentException {
        return Result.ok(encoded);
    }

    @Override
    public boolean canDeserialize(T encoded) {
        return true;
    }
    
}
