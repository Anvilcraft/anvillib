package net.anvilcraft.anvillib.ae2.impl;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.util.AECableType;
import appeng.me.GridAccessException;
import appeng.me.helpers.IGridProxyable;
import net.anvilcraft.anvillib.ae2.AEIntegration;
import net.anvilcraft.anvillib.api.types.Direction;
import net.anvilcraft.anvillib.api.types.IDirection;
import net.anvilcraft.anvillib.api.units.IEnergyAdapter;
import net.anvilcraft.anvillib.api.units.IEnergyUnit;
import net.anvilcraft.anvillib.energy.BaseEnergyAdapter;
import net.anvilcraft.anvillib.util.DirectionConverter;
import net.minecraftforge.common.util.ForgeDirection;

public class AEEnergyAdapter extends BaseEnergyAdapter {

    @Override
    public IEnergyUnit getDefaultUnit() {
        return AEIntegration.ENERGY_UNIT;
    }

    @Override
    public Class<?>[] getRelatedInterfaces() {
        return new Class[]{
            IGridProxyable.class,
            IEnergyGrid.class,
            IEnergySource.class,
            IAEPowerStorage.class,
            IGridHost.class
        };
    }

    @Override
    public boolean canConnect(Object obj, IDirection from, Object source, int energyFlowFlags) {
        return obj instanceof IGridHost ? ((IGridHost) obj).getCableConnectionType(DirectionConverter.INSTANCE.convertFrom(from).get()) != AECableType.NONE : false;
    }

    @Override
    public double receiveEnergy(Object obj, IDirection from, double receive, boolean doReceive) {
        if (obj instanceof IAEPowerStorage) {
            return receive - ((IAEPowerStorage) obj).injectAEPower(
                    receive,
                    doReceive ? Actionable.MODULATE : Actionable.SIMULATE
                );
        }
        IEnergyGrid grid = getGrid(obj, DirectionConverter.INSTANCE.convertFrom(from).get());
        if (grid != null) {
            double overflow = grid.injectPower(
                receive,
                doReceive ? Actionable.MODULATE : Actionable.SIMULATE
            );
            return doReceive ? receive : receive - overflow;
        }
        return 0;
    }

    @Override
    public double extractEnergy(Object obj, IDirection from, double extract, boolean doExtract) {
        IEnergySource src = obj instanceof IEnergySource ? (IEnergySource) obj : getGrid(obj, DirectionConverter.INSTANCE.convertFrom(from).get());
        return src != null ? src.extractAEPower(
                extract, 
                doExtract ? Actionable.MODULATE : Actionable.SIMULATE, 
                PowerMultiplier.ONE
            ) : 0;
    }

    @Override
    public double getEnergy(Object obj, IDirection from) {
        if (obj instanceof IAEPowerStorage) {
            return ((IAEPowerStorage) obj).getAECurrentPower();
        }
        IEnergyGrid grid = getGrid(obj, DirectionConverter.INSTANCE.convertFrom(from).get());        
        return grid != null ? grid.getStoredPower() : 0;
    }

    @Override
    public double getEnergyCapacity(Object obj, IDirection from) {
        if (obj instanceof IAEPowerStorage) {
            return ((IAEPowerStorage) obj).getAEMaxPower();
        }
        IEnergyGrid grid = getGrid(obj, DirectionConverter.INSTANCE.convertFrom(from).get());        
        return grid != null ? grid.getMaxStoredPower() : 0;
    }

    private IEnergyGrid getGrid(Object obj, ForgeDirection from) {
        IEnergyGrid grid = null;
        if (obj instanceof IEnergyGrid) {
            grid = (IEnergyGrid) obj;
        } else if (obj instanceof IGridProxyable) {
            try {
                grid = ((IGridProxyable) obj).getProxy().getEnergy();
            } catch (GridAccessException e) {
                
            }
        } else if (obj instanceof IGridHost && ((IGridHost) obj).getGridNode(from) != null) {
            IGridNode node = ((IGridHost) obj).getGridNode(from);
            grid = node.getGrid().getCache(IEnergyGrid.class);
        }

        return grid;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T createWrapper(Object source, Class<T> iface, IEnergyAdapter sourceAdapter) {
        if (IAEPowerStorage.class.isAssignableFrom(iface)) {
            return (T) new Wrapper(sourceAdapter, source);
        }
        return null;
    }

    private class Wrapper implements IAEPowerStorage {

        IEnergyAdapter system;
        Object obj;

        public Wrapper(IEnergyAdapter system, Object obj) {
            this.system = system;
            this.obj = obj;
        }

        @Override
        public double extractAEPower(double amt, Actionable mode, PowerMultiplier usePowerMultiplier) {
            return system.extractEnergy(
                obj, 
                AEEnergyAdapter.this.getDefaultUnit(),
                Direction.UNKNOWN, 
                amt, 
                mode == Actionable.MODULATE
            );
        }

        @Override
        public double injectAEPower(double amt, Actionable mode) {
            return amt - system.receiveEnergy(
                obj, 
                AEEnergyAdapter.this.getDefaultUnit(),
                Direction.UNKNOWN, 
                amt, 
                mode == Actionable.MODULATE
            );
        }

        @Override
        public double getAEMaxPower() {
            return system.getEnergy(obj, AEEnergyAdapter.this.getDefaultUnit(), Direction.UNKNOWN);
        }

        @Override
        public double getAECurrentPower() {
            return system.getEnergyCapacity(obj, AEEnergyAdapter.this.getDefaultUnit(), Direction.UNKNOWN);
        }

        @Override
        public boolean isAEPublicPowerStorage() {
            return false;
        }

        @Override
        public AccessRestriction getPowerFlow() {
            return AccessRestriction.READ_WRITE;
        }

    }
    
}
