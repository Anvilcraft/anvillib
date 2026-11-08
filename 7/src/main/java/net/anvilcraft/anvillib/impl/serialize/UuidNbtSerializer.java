package net.anvilcraft.anvillib.impl.serialize;

import java.util.UUID;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.types.Result;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;

public class UuidNbtSerializer implements IObjectSerializer<NBTTagList, UUID> {

    public static final IObjectSerializer<NBTTagList, UUID> INSTANCE = new UuidNbtSerializer();

    @Override
    public Result<NBTTagList, Exception> serialize(UUID uuid) {
        NBTTagList nbt = new NBTTagList();

        nbt.appendTag(new NBTTagLong(uuid.getMostSignificantBits()));
        nbt.appendTag(new NBTTagLong(uuid.getLeastSignificantBits()));

        return Result.ok(nbt);
    }

    @Override
    public Result<UUID, Exception> deserialize(NBTTagList nbt) throws IllegalArgumentException {
        if (!canDeserialize(nbt)) return Result.error(new IllegalArgumentException());

        return Result.ok(new UUID(
            ((NBTTagLong) nbt.tagList.get(0)).func_150291_c(),
            ((NBTTagLong) nbt.tagList.get(1)).func_150291_c()
        ));
    }

    @Override
    public boolean canDeserialize(NBTTagList nbt) {
        return nbt.tagCount() == 2 && nbt.tagList.get(0) instanceof NBTTagLong
            && nbt.tagList.get(1) instanceof NBTTagLong;
    }
    
}
