package net.anvilcraft.anvillib.api.serialize;

import net.anvilcraft.anvillib.api.types.Result;

public interface IObjectSerializer<E, D> {
    
    Result<E, Exception> serialize(D object);

    Result<D, Exception> deserialize(E encoded);

    boolean canDeserialize(E encoded);

}