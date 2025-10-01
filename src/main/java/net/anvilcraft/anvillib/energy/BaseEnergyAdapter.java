package net.anvilcraft.anvillib.energy;

import net.anvilcraft.anvillib.api.units.IEnergyAdapter;
import net.anvilcraft.anvillib.api.units.IEnergyUnit;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BaseEnergyAdapter implements IEnergyAdapter {

    protected abstract double receiveEnergy(Object obj, ForgeDirection from, double receive, boolean doReceive);

	protected abstract double extractEnergy(Object obj, ForgeDirection from, double extract, boolean doExtract);

	protected abstract double getEnergy(Object obj, ForgeDirection from);

	protected abstract double getEnergyCapacity(Object obj, ForgeDirection from);

    protected boolean setEnergy(Object obj, ForgeDirection from, double energy) {
        double delta = energy - this.getEnergy(obj, from);
        if (delta > 0) {
            return delta == this.receiveEnergy(obj, from, delta, true);
        } else if (delta < 0) {
            return -delta == this.extractEnergy(obj, from, -delta, true);
        }
        return delta == 0;
    }

    @Override
    public boolean canHandle(Class<?> type) {
        for (Class<?> t : getRelatedInterfaces()) {
            if (t.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canHandle(Object obj) {
        return obj != null ? canHandle(obj.getClass()) : false;
    }

    @Override
    public double extractEnergy(Object obj, IEnergyUnit unit, ForgeDirection from, double extract, boolean doExtract) {
        return getDefaultUnit().convertTo(unit, extractEnergy(obj, from, unit.convertTo(getDefaultUnit(), extract), doExtract));
    }

    @Override
    public double getEnergy(Object obj, IEnergyUnit unit, ForgeDirection from) {
        return getDefaultUnit().convertTo(unit, getEnergy(obj, from));
    }

    @Override
    public double getEnergyCapacity(Object obj, IEnergyUnit unit, ForgeDirection from) {
        return getDefaultUnit().convertTo(unit, getEnergyCapacity(obj, from));
    }

    @Override
    public double receiveEnergy(Object obj, IEnergyUnit unit, ForgeDirection from, double receive, boolean doReceive) {
        return getDefaultUnit().convertTo(unit, receiveEnergy(obj, from, unit.convertTo(getDefaultUnit(), receive), doReceive));
    }

    @Override
    public boolean setEnergy(Object obj, IEnergyUnit unit, ForgeDirection from, double energy) {
        return setEnergy(obj, from, unit.convertTo(getDefaultUnit(), energy));
    }
    
}
