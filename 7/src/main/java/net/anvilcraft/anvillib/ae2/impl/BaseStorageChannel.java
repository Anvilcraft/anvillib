package net.anvilcraft.anvillib.ae2.impl;

import java.io.IOException;
import java.util.function.Function;

import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public abstract class BaseStorageChannel<T extends IAEStack<T>> implements IStorageChannel<T> {

    private Class<T> type;
    private Function<Object, T> factory;
    private Function<ByteBuf, T> packetReader;
    private Function<NBTTagCompound, T> nbtReader;

    public BaseStorageChannel(Class<T> type, Function<Object, T> factory, Function<ByteBuf, T> packetReader,
            Function<NBTTagCompound, T> nbtReader) {
        this.type = type;
        this.factory = factory;
        this.packetReader = packetReader;
        this.nbtReader = nbtReader;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public IItemList<T> createList() {
        return new AEItemList<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T createStack(Object input) {
        if (input == null) {
            return null;
        } else if (type.isAssignableFrom(input.getClass())) {
            return ((T)input).copy();
        } else if (this.factory != null) {
            return factory.apply(input);
        }
        return null;
    }

    @Override
    public T readFromPacket(ByteBuf input) throws IOException {
        return packetReader.apply(input);
    }

    @Override
    public T createFromNBT(NBTTagCompound nbt) {
        return nbtReader.apply(nbt);
    }
    
}
