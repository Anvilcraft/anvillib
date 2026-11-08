package net.anvilcraft.anvillib.impl.serialize;

import java.util.Base64;
import java.util.regex.Pattern;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.types.Result;

public class Base64Encoder implements IObjectSerializer<String, String> {

    public static IObjectSerializer<String, String> INSTANCE = new Base64Encoder();
    public static Pattern BASE64_PATTERN = Pattern.compile("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");

    @Override
    public Result<String, Exception> serialize(String object) {
        return Result.ok(Base64.getEncoder().encodeToString(object.getBytes()));
    }

    @Override
    public Result<String, Exception> deserialize(String encoded) {
        return Result.create(encoded, (i) -> new String(Base64.getDecoder().decode(i)));
    }

    @Override
    public boolean canDeserialize(String encoded) {
        return BASE64_PATTERN.matcher(encoded).matches();
    }
    
}
