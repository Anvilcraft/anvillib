package net.anvilcraft.anvillib.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import io.netty.buffer.ByteBuf;
import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.impl.serialize.NumericEnumSerializer;
import net.anvilcraft.anvillib.impl.serialize.UuidNbtSerializer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class AnvilUtil {
    public static final Random rand = new Random();
    private static final Map<Class<? extends Enum<?>>, IObjectSerializer<Integer, ? extends Enum<?>>> enumSerializerCache = new HashMap<>(); 

    @SuppressWarnings({ "rawtypes", "unchecked", "ALEC" })
    public static <T extends Enum<T>> T enumFromInt(Class<T> clazz, int n) {
        IObjectSerializer<Integer, ? extends Enum<?>> serializer = enumSerializerCache.computeIfAbsent(
            clazz, 
            (c) -> NumericEnumSerializer.of((Class) c)
        );
        return (T) serializer.deserialize(n).getOrNull();
    }

    public static NBTTagList uuidToNBT(UUID uuid) {
        return UuidNbtSerializer.INSTANCE.serialize(uuid).get();
    }

    public static UUID uuidFromNBT(NBTTagList nbt) {
        if (!UuidNbtSerializer.INSTANCE.canDeserialize(nbt))
            return null;

        return UuidNbtSerializer.INSTANCE.deserialize(nbt).getOrNull();
    }

    public static void writeStringToPacket(String s, ByteBuf buf) {
        try {
            byte[] bytes = s.getBytes("ISO-8859-1");
            int length = bytes.length;
            buf.writeInt(length);
            buf.writeBytes(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readStringFromPacket(ByteBuf buf) {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        try {
            return new String(bytes, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Drops all items in the given IInventory into the world.
     */
    public static void dropInventoryContents(IInventory inv) {
        TileEntity te = (TileEntity) inv;

        IntStream.range(0, inv.getSizeInventory())
            .mapToObj(inv::getStackInSlot)
            .filter(stack -> (stack != null && stack.stackSize > 0))
            .map(stack -> {
                EntityItem ent = new EntityItem(
                    te.getWorldObj(),
                    te.xCoord + rand.nextFloat() * 0.8f + 0.1f,
                    te.yCoord + rand.nextFloat() * 0.8f + 0.1f,
                    te.zCoord + rand.nextFloat() * 0.8f + 0.1f,
                    stack.copy()
                );
                ent.motionX = rand.nextGaussian() * 0.05f;
                ent.motionY = rand.nextGaussian() * 0.05f + 0.2f;
                ent.motionZ = rand.nextGaussian() * 0.05f;
                return ent;
            })
            .forEach(te.getWorldObj()::spawnEntityInWorld);
    }
}
