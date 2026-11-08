package net.anvilcraft.anvillib.impl.serialize;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.serialize.ISerializedRead;
import net.anvilcraft.anvillib.api.types.Result;

public class StringReadSerializer implements IObjectSerializer<String, ISerializedRead> {

    public static IObjectSerializer<String, ISerializedRead> INSTANCE = new StringReadSerializer();

    @Override
    public Result<String, Exception> serialize(ISerializedRead object) {
        StringWriter writer = new StringWriter();
        try {
            object.write(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok(writer.toString());
    }

    @Override
    public Result<ISerializedRead, Exception> deserialize(String encoded) {
        return Result.ok(ISerializedRead.fromReader(new StringReader(encoded)));
    }

    @Override
    public boolean canDeserialize(String encoded) {
        return true;
    }
    
}
