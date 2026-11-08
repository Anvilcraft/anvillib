package net.anvilcraft.anvillib.impl.serialize;

import java.util.Optional;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.serialize.IObjectType;
import net.anvilcraft.anvillib.api.types.Result;

public class OptionalSerializer<T, S> implements IObjectSerializer<T, Optional<S>> {

    IObjectSerializer<T, S> subSerializer;
    IObjectType<T> manipulator;

    public OptionalSerializer(IObjectSerializer<T, S> subSerializer, IObjectType<T> manipulator) {
        this.subSerializer = subSerializer;
        this.manipulator = manipulator;
    }

    @Override
    public Result<T, Exception> serialize(Optional<S> object) {
        if (object.isPresent()) {
            return subSerializer.serialize(object.get());
        } else {
            return Result.ok(manipulator.createNull());
        }
    }

    @Override
    public Result<Optional<S>, Exception> deserialize(T encoded) {
        if (manipulator.isNull(encoded)) {
            return Result.ok(Optional.empty());
        } else {
            return subSerializer.deserialize(encoded).map((t) -> Optional.of(t));
        }
    }

    @Override
    public boolean canDeserialize(T encoded) {
        return subSerializer.canDeserialize(encoded);
    }
    
}
