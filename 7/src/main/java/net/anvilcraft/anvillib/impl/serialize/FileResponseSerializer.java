package net.anvilcraft.anvillib.impl.serialize;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.types.Result;

public class FileResponseSerializer implements IObjectSerializer<JsonElement, List<URL>> {

    public static final IObjectSerializer<JsonElement, List<URL>> INSTANCE = new FileResponseSerializer();

    @Override
    public boolean canDeserialize(JsonElement json) {
        return json.isJsonObject();
    }

    @Override
    public Result<List<URL>, Exception> deserialize(JsonElement json) {
        return Result.create(json, JsonElement::getAsJsonObject)
            .mapError(e -> (Exception) e)
            .map(o -> o.get("url"))
            .map(JsonElement::getAsJsonArray)
            .map(a -> {
                List<URL> urls = new ArrayList<>(a.size());
                for (JsonElement element : a) {
                    urls.add(Result.create(element, JsonElement::getAsString)
                        .mapError(e -> (Exception) e)
                        .flatMap(URLSerializer.INSTANCE::deserialize)
                        .getOrThrow());
                }
                return urls;
            });
    }

    @Override
    public Result<JsonElement, Exception> serialize(List<URL> urls) {
        JsonArray array = new JsonArray();
        urls.stream()
            .map(URLSerializer.INSTANCE::serialize)
            .map(r -> r.map(JsonPrimitive::new).get())
            .forEach(array::add);
        JsonObject obj = new JsonObject();
        obj.add("url", array);
        return Result.ok(obj);
    }
    
}
