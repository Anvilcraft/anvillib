package net.anvilcraft.anvillib.ae2.impl;

import java.io.IOException;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IAETagCompound;
import io.netty.buffer.ByteBuf;
import net.anvilcraft.anvillib.ae2.AEIntegration;
import net.anvilcraft.anvillib.api.ae2.channel.IEnergyStorageChannel;
import net.anvilcraft.anvillib.api.ae2.stack.IEnergyStack;
import net.anvilcraft.anvillib.api.units.IEnergyUnit;
import net.anvilcraft.anvillib.registries.UnitRegistry;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.minecraft.nbt.NBTTagCompound;

public class EnergyStack extends BaseStack<IEnergyStack> implements IEnergyStack {

    private double amount = 0.0;
    private IEnergyUnit type;

    public EnergyStack(IEnergyUnit type) {
        super(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));
        this.type = type;
    }

    @Override
    public void add(IEnergyStack is) {
        if (is == null) {
            return;
        }
        this.amount += is.getEnergy();
        this.setCountRequestable(
            this.getCountRequestable() + is.getCountRequestable()
        );
        this.setCraftable(this.isCraftable() || is.isCraftable());
    }

    @Override
    public long getStackSize() {
        return (long) this.amount;
    }

    @Override
    public IEnergyStack setStackSize(long stackSize) {
        this.amount = stackSize;
        return this;
    }

    @Override
    public IEnergyStack reset() {
        this.amount = 0;
        this.isCraftable = false;
        this.requestable = 0;
        return this;
    }

    @Override
    public boolean isMeaningful() {
        return this.amount != 0 || this.requestable > 0 || this.isCraftable;
    }

    public static IEnergyStack readFromNBT(NBTTagCompound i) {
        if (!i.hasKey("type")) return null;
        String typeId = i.getString("type");
        IEnergyUnit type = (IEnergyUnit) UnitRegistry.INSTANCE.get(typeId);
        IEnergyStack stack = new EnergyStack(type);
        stack.setCraftable(i.getBoolean("isCraftable"));
        stack.setEnergy(i.getDouble("amount"));
        stack.setCountRequestable(i.getLong("requestable"));
        return stack;
    }

    @Override
    public void writeToNBT(NBTTagCompound i) {
        i.setBoolean("isCraftable", this.isCraftable);
        i.setDouble("amount", this.amount);
        i.setLong("requestable", this.requestable);
        i.setString("type", type.getID());
    }

    @Override
    public boolean fuzzyComparison(Object st, FuzzyMode mode) {
        if (st instanceof IEnergyStack) {
            return this.getEnergyType() == ((IEnergyStack)st).getEnergyType();
        } else if (st instanceof IEnergyUnit) {
            return this.getEnergyType() == st;
        }
        return false;
    }

    public static IEnergyStack readFromPacket(ByteBuf data) {
        String typeId = AnvilUtil.readStringFromPacket(data);
        IEnergyUnit system = (IEnergyUnit) UnitRegistry.INSTANCE.get(typeId);
        IEnergyStack stack = new EnergyStack(system);
        stack.setEnergy(data.readDouble());
        stack.setCraftable(data.readBoolean());
        stack.setCountRequestable(data.readLong());
        return stack;
    }

    @Override
    public void writeToPacket(ByteBuf data) throws IOException {
        AnvilUtil.writeStringToPacket(type.getID(), data);
        data.writeDouble(amount);
        data.writeBoolean(isCraftable);
        data.writeLong(requestable);
    }

    @Override
    public IEnergyStack copy() {
        IEnergyStack stack = new EnergyStack(this.type);
        stack.setEnergy(this.getEnergy());
        stack.setCraftable(this.isCraftable());
        stack.setCountRequestable(this.getCountRequestable());
        return stack;
    }

    @Override
    public IAETagCompound getTagCompound() {
        return null;
    }

    @Override
    public double getEnergy() {
        return this.amount;
    }

    @Override
    public void setEnergy(double energy) {
        this.amount = energy;
    }

    @Override
    public double getAsAE() {
        return this.getEnergyType().convertTo(AEIntegration.ENERGY_UNIT, this.amount);
    }

    @Override
    public IEnergyUnit getEnergyType() {
        return this.type;
    }
    
}
