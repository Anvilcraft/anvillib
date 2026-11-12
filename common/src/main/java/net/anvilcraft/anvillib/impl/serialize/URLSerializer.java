package net.anvilcraft.anvillib.impl.serialize;

import java.net.URL;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.types.Result;

public class URLSerializer implements IObjectSerializer<String, URL> {

    public static final IObjectSerializer<String, URL> INSTANCE = new URLSerializer();

    @Override
    public Result<String, Exception> serialize(URL object) {
        return Result.ok(object.toString());
    }

    @Override
    public Result<URL, Exception> deserialize(String encoded) {
        return Result.create(encoded, URL::new);
    }

    @Override
    public boolean canDeserialize(String encoded) {
        return true;
    }
    
}
