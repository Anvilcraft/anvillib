package net.anvilcraft.anvillib.ae2.impl;

import java.io.IOException;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IAETagCompound;
import io.netty.buffer.ByteBuf;
import net.anvilcraft.anvillib.api.ae2.channel.IMatterStorageChannel;
import net.anvilcraft.anvillib.api.ae2.stack.IMatterStack;
import net.anvilcraft.anvillib.api.units.IUnit;
import net.anvilcraft.anvillib.registries.UnitRegistry;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.minecraft.nbt.NBTTagCompound;

public class MatterStack extends BaseStack<IMatterStack> implements IMatterStack {

    private long amount = 0;
    private IUnit type; 

    public MatterStack(IUnit type) {
        super(AEApi.instance().storage().getStorageChannel(IMatterStorageChannel.class));
        this.type = type;
    }

    @Override
    public void add(IMatterStack is) {
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
    public IMatterStack setStackSize(long stackSize) {
        this.amount = stackSize;
        return this;
    }

    @Override
    public IMatterStack reset() {
        this.amount = 0;
        this.isCraftable = false;
        this.requestable = 0;
        return this;
    }

    public static IMatterStack readFromNBT(NBTTagCompound i) {
        if (!i.hasKey("type")) return null;
        String typeId = i.getString("type");
        IUnit type = UnitRegistry.INSTANCE.get(typeId);
        IMatterStack stack = new MatterStack(type);
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
        if (st instanceof IMatterStack) {
            return this.getMatterType() == ((IMatterStack)st).getMatterType();
        } else if (st instanceof IUnit) {
            return this.getMatterType() == st;
        }
        return false;
    }

    public static IMatterStack readFromPacket(ByteBuf data) {
        String typeId = AnvilUtil.readStringFromPacket(data);
        IUnit system = UnitRegistry.INSTANCE.get(typeId);
        IMatterStack stack = new MatterStack(system);
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
    public IMatterStack copy() {
        IMatterStack stack = new MatterStack(this.type);
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
    public IUnit getMatterType() {
        return this.type;
    }
    
}
