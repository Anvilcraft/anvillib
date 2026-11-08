package net.anvilcraft.anvillib.impl.serialize;

import java.util.UUID;
import java.util.regex.Pattern;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.types.Result;

public class UUIDSerializer implements IObjectSerializer<String, UUID> {

    public static IObjectSerializer<String, UUID> INSTANCE = new UUIDSerializer();
    public static Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$");

    @Override
    public Result<String, Exception> serialize(UUID object) {
        return Result.ok(object.toString());

    }

    @Override
    public Result<UUID, Exception> deserialize(String encoded) {
        return Result.create(encoded, (i) -> UUID.fromString(i));
    }

    @Override
    public boolean canDeserialize(String encoded) {
        return UUID_PATTERN.matcher(encoded).matches();
    }
    
}
