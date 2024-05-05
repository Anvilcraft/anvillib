package net.anvilcraft.anvillib.util;

import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;

public class AnvilUtil {
    public static final Random rand = new Random();

    public static <T extends Enum<T>> T enumFromInt(Class<T> clazz, int n) {
        T[] values = clazz.getEnumConstants();
        if (n < 0 || n >= values.length)
            return null;
        return values[n];
    }

    public static NBTTagList uuidToNBT(UUID uuid) {
        NBTTagList nbt = new NBTTagList();

        nbt.appendTag(new NBTTagLong(uuid.getMostSignificantBits()));
        nbt.appendTag(new NBTTagLong(uuid.getLeastSignificantBits()));

        return nbt;
    }

    public static UUID uuidFromNBT(NBTTagList nbt) {
        if (nbt.tagCount() != 2 || !(nbt.tagList.get(0) instanceof NBTTagLong)
            || !(nbt.tagList.get(1) instanceof NBTTagLong))
            return null;

        return new UUID(
            ((NBTTagLong) nbt.tagList.get(0)).func_150291_c(),
            ((NBTTagLong) nbt.tagList.get(1)).func_150291_c()
        );
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
