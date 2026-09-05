package net.anvilcraft.anvillib.ae2.impl;

import java.io.IOException;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IAETagCompound;
import io.netty.buffer.ByteBuf;
import net.anvilcraft.anvillib.api.ae2.channel.IManaStorageChannel;
import net.anvilcraft.anvillib.api.ae2.stack.IManaStack;
import net.anvilcraft.anvillib.api.units.IUnit;
import net.anvilcraft.anvillib.registries.UnitRegistry;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.minecraft.nbt.NBTTagCompound;

public class ManaStack extends BaseStack<IManaStack> implements IManaStack {

    private long amount = 0;
    private IUnit type;

    public ManaStack(IUnit type) {
        super(AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class));
        this.type = type;
    }

    @Override
    public void add(IManaStack is) {
        if (is == null) {
            return;
        }
        this.amount += is.getStackSize();
        this.setCountRequestable(
            this.getCountRequestable() + is.getCountRequestable()
        );
        this.setCraftable(this.isCraftable() || is.isCraftable());
    }

    @Override
    public long getStackSize() {
        return this.amount;
    }

    @Override
    public IManaStack setStackSize(long stackSize) {
        this.amount = stackSize;
        return this;
    }

    @Override
    public IManaStack reset() {
        this.amount = 0;
        this.isCraftable = false;
        this.requestable = 0;
        return this;
    }

    public static IManaStack readFromNBT(NBTTagCompound i) {
        if (!i.hasKey("type")) return null;
        String typeId = i.getString("type");
        IUnit type = UnitRegistry.INSTANCE.get(typeId);
        IManaStack stack = new ManaStack(type);
        stack.setCraftable(i.getBoolean("isCraftable"));
        stack.setStackSize(i.getLong("amount"));
        stack.setCountRequestable(i.getLong("requestable"));
        return stack;
    }

    @Override
    public void writeToNBT(NBTTagCompound i) {
        i.setBoolean("isCraftable", this.isCraftable);
        i.setLong("amount", this.amount);
        i.setLong("requestable", this.requestable);
        i.setString("type", type.getID());
    }

    @Override
    public boolean fuzzyComparison(Object st, FuzzyMode mode) {
        if (st instanceof IManaStack) {
            return this.getManaType() == ((IManaStack)st).getManaType();
        } else if (st instanceof IUnit) {
            return this.getManaType() == st;
        }
        return false;
    }

    public static IManaStack readFromPacket(ByteBuf data) {
        String typeId = AnvilUtil.readStringFromPacket(data);
        IUnit system = UnitRegistry.INSTANCE.get(typeId);
        IManaStack stack = new ManaStack(system);
        stack.setStackSize(data.readLong());
        stack.setCraftable(data.readBoolean());
        stack.setCountRequestable(data.readLong());
        return stack;
    }

    @Override
    public void writeToPacket(ByteBuf data) throws IOException {
        AnvilUtil.writeStringToPacket(type.getID(), data);
        data.writeLong(amount);
        data.writeBoolean(isCraftable);
        data.writeLong(requestable);
    }

    @Override
    public IManaStack copy() {
        IManaStack stack = new ManaStack(this.type);
        stack.setStackSize(this.getStackSize());
        stack.setCraftable(this.isCraftable());
        stack.setCountRequestable(this.getCountRequestable());
        return stack;
    }

    @Override
    public IAETagCompound getTagCompound() {
        return null;
    }

    @Override
    public IUnit getManaType() {
        return this.type;
    }
    
}
