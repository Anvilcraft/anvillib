package net.anvilcraft.anvillib.impl.serialize;

import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.serialize.ISerializedRead;
import net.anvilcraft.anvillib.api.types.Result;

public class GsonSerializer implements IObjectSerializer<ISerializedRead, JsonElement> {

    public static IObjectSerializer<ISerializedRead, JsonElement> RW = new GsonSerializer();
    public static IObjectSerializer<String, JsonElement> STRING = new CombinedSerializer<>(StringReadSerializer.INSTANCE, RW);
    public static IObjectSerializer<ISerializedRead, JsonElement> PRETTY = new GsonSerializer(new GsonBuilder().setPrettyPrinting().create());

    private Gson gson;

    public GsonSerializer() {
        this.gson = new GsonBuilder().create();
    }
    

    public GsonSerializer(Gson gson) {
        this.gson = gson;
    }


    @Override
    public Result<ISerializedRead, Exception> serialize(JsonElement object) {
        return Result.ok((writer) -> {
            gson.toJson(object, writer);
            writer.close();
        });
    }

    @Override
    public Result<JsonElement, Exception> deserialize(ISerializedRead encoded) {
        try {
            Reader reader = encoded.read();
            JsonElement result = gson.fromJson(reader, JsonElement.class);
            reader.close();
            return Result.ok(result);
        } catch (JsonSyntaxException | JsonIOException | IOException e) {
            return Result.error(new IllegalArgumentException(e));
        }
    }

    @Override
    public boolean canDeserialize(ISerializedRead encoded) {
        return true;
    }
    
}
