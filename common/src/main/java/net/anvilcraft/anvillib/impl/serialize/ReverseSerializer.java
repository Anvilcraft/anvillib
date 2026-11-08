package net.anvilcraft.anvillib.impl.serialize;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.types.Result;

public class ReverseSerializer<E, D> implements IObjectSerializer<E, D> {

    private IObjectSerializer<D, E> inner;

    public ReverseSerializer(IObjectSerializer<D, E> inner) {
        this.inner = inner;
    }    

    @Override
    public Result<D, Exception> deserialize(E encoded) {
        return inner.serialize(encoded);
    }

    @Override
    public Result<E, Exception> serialize(D object) {
        return inner.deserialize(object);
    }

    @Override
    public boolean canDeserialize(E encoded) {
        return true;
    }
    
}
