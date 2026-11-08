package net.anvilcraft.anvillib.impl.serialize;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.types.Result;

public class CombinedSerializer<E, I, D> implements IObjectSerializer<E, D> {

    private IObjectSerializer<E, I> backend;
    private IObjectSerializer<I, D> frontend;

    public CombinedSerializer(IObjectSerializer<E, I> backend, IObjectSerializer<I, D> frontend) {
        this.backend = backend;
        this.frontend = frontend;
    }

    @Override
    public Result<E, Exception> serialize(D object) {
        return frontend.serialize(object).flatMap(backend::serialize);
    }

    @Override
    public Result<D, Exception> deserialize(E encoded) {
        return backend.deserialize(encoded).flatMap(frontend::deserialize);
    }

    @Override
    public boolean canDeserialize(E encoded) {
        return backend.canDeserialize(encoded);
    }
    
}
