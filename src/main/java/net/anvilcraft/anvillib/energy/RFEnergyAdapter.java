package net.anvilcraft.anvillib.energy;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.anvilcraft.anvillib.api.units.IEnergyAdapter;
import net.anvilcraft.anvillib.api.units.IEnergyUnit;
import net.minecraftforge.common.util.ForgeDirection;

public class RFEnergyAdapter extends BaseEnergyAdapter {

    RFEnergyUnit unit;

    public RFEnergyAdapter(RFEnergyUnit unit) {
        this.unit = unit;
    }

    @Override
    public IEnergyUnit getDefaultUnit() {
        return unit;
    }

    @Override
    public Class<?>[] getRelatedInterfaces() {
        return new Class[]{
            IEnergyHandler.class,
            IEnergyReceiver.class,
            IEnergyProvider.class
        };
    }

    @Override
    public boolean canConnect(Object obj, ForgeDirection from, Object source, int energyFlowFlags) {
        boolean requireInput = (energyFlowFlags & CAN_INPUT) == CAN_INPUT;
        boolean requireOutput = (energyFlowFlags & CAN_OUTPUT) == CAN_OUTPUT;
        return obj instanceof IEnergyConnection ? 
            ((IEnergyConnection) obj).canConnectEnergy(from) 
            && (!requireInput || obj instanceof IEnergyReceiver)
            && (!requireOutput || obj instanceof IEnergyProvider) : false;
    }

    @Override
    public double receiveEnergy(Object obj, ForgeDirection from, double receive, boolean doReceive) {
        if (obj instanceof IEnergyReceiver) {
            return ((IEnergyReceiver)obj).receiveEnergy(from, (int)receive, !doReceive);
        }
        return 0;
    }

    @Override
    public double extractEnergy(Object obj, ForgeDirection from, double extract, boolean doExtract) {
        if (obj instanceof IEnergyProvider) {
            return ((IEnergyProvider)obj).extractEnergy(from, (int)extract, !doExtract);
        }
        return 0;
    }

    @Override
    public double getEnergy(Object obj, ForgeDirection from) {
        if (obj instanceof IEnergyHandler) {
            return ((IEnergyHandler)obj).getEnergyStored(from);
        }
        return 0;
    }

    @Override
    public double getEnergyCapacity(Object obj, ForgeDirection from) {
        if (obj instanceof IEnergyHandler) {
            return ((IEnergyHandler)obj).getMaxEnergyStored(from);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T createWrapper(Object source, Class<T> iface, IEnergyAdapter sourceSystem) {
        if (IEnergyReceiver.class.isAssignableFrom(iface) || IEnergyProvider.class.isAssignableFrom(iface)) {
            return (T) new Wrapper(sourceSystem, source);
        }
        return null;
    }

    private class Wrapper implements IEnergyHandler {

        private IEnergyAdapter adapter;
        private Object obj;
        
        public Wrapper(IEnergyAdapter adapter, Object obj) {
            this.adapter = adapter;
            this.obj = obj;
        }

        @Override
        public boolean canConnectEnergy(ForgeDirection var1) {
            return adapter.canConnect(obj, var1, this, 0);
        }

        @Override
        public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
            return (int) adapter.receiveEnergy(obj, RFEnergyAdapter.this.unit, from, maxReceive, !simulate);
        }

        @Override
        public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
            return (int) adapter.extractEnergy(obj, RFEnergyAdapter.this.unit, from, maxExtract, !simulate);
        }

        @Override
        public int getEnergyStored(ForgeDirection from) {
            return (int) adapter.getEnergy(obj, RFEnergyAdapter.this.unit, from);
        }

        @Override
        public int getMaxEnergyStored(ForgeDirection from) {
            return (int) adapter.getEnergyCapacity(obj, RFEnergyAdapter.this.unit, from);
        }
        
    }
    
}
